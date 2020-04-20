package model

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import models.Tables._
import scala.concurrent.Future
import org.mindrot.jbcrypt.BCrypt //import mindrot in build.sbt
import com.typesafe.sslconfig.ssl.FakeChainedKeyStore.User
import collection.mutable

class HordeDatabaseModel(db: Database)(implicit ec: ExecutionContext) {
    private val cost = List[Int](1, 100, 1000, 10000, 250000, 1000000, 10000000, 250000000, 1000000000)
    //level upgrade cost multiplier: all 1.35 except Dr. Lewis which is 1
    //collection rate multiplier: all 1.1 except Dr. Lewis which is 1
    private val conversionRate = List[Double](100.0, 10.0, 1.0, 1.05, 1.0/75, 1.0/700, 1.0/15000, 1.0/40000, 1)
    //cap: all 100K except Lewis which is 1
    private val names = List[String]("Rocks and Minerals", "Junk Food", "90s Paraphernalia", "Yarn", "Stuffed Animals", "Cats", "Music Boxes", "Coding Textbooks")

    //WARNING - Gold may have to be a double
    //... doubles, it's all doubles


    def validateUser(username:String,password:String):Future[Option[Int]] = {
        val matches = db.run(Users.filter(userRow => userRow.username === username).result)
        matches.map(userRows => userRows.headOption.flatMap{
            userRow => if (BCrypt.checkpw(password,userRow.password)) Some(userRow.id) else None
        })
    }

    def createUser(username:String,password:String):Future[Boolean] = {
        val matches = db.run(Users.filter(userRow => userRow.username === username).result)
        matches.flatMap{ userRows => 
            if(userRows.nonEmpty) {
                Future.successful(false)
            } else {
                db.run(Users += UsersRow(-1, username, BCrypt.hashpw(password, BCrypt.gensalt()), 1))
                    //.map(addCount => addCount>0)
            }
        }
        val userId = db.run((for {user <- Users if user.username === username} yield {user.id}).result)
        //needs to initialise hoards for the user
        var i = 0
        var unlocked = true
        for(i <- 1 to 9) {
            if(i > 1) unlocked = false
            userId.flatMap { ids =>
                db.run(Hoard += HoardRow(-1, ids.head, i, 
                        /*cost*/cost(i-1), 
                        /*HoardLevel*/0, 
                        /*HoardItems*/0, 
                        /*ProductionSpeed*/0, 
                        /*Gold Conversion*/conversionRate(i-1),
                        /*unlocked*/unlocked))
            }
        }
        Future.successful(true)
        //maybe upgrades too, but that could be done upon unlock
        //if I do do it though, I just need the list of upgrades
    }

    def getUserInfo(userid:Int):Future[(Option[Int], Seq[Boolean], Seq[Boolean])] =
    {
        //get basic user data
            //currently an option, could not be
        var gold = db.run((for {user <- Users if user.id === userid} yield {user.gold}).result).map(userRows => userRows.headOption)
        //unlocked hoards
        var hoards = db.run((for {hoard <- Hoard if hoard.userId == userid} yield {hoard.unlocked}).result)
        //unlocked universal upgrades
        var upgrades = db.run((for {uUpgrade <- Univupgrades if uUpgrade.userId == userid} yield {uUpgrade.unlocked}).result)
        for{ 
            g <- gold
            h <- hoards
            u <- upgrades
         } yield {
            (g, h, u)
         }
    }
    def getHoardInfo(userid:Int, hoardType:Int):Future[(Int, Int, Int, Double, Double, Double, Boolean)] = {
        //reult of all hoards with userid and hoardType
        val matches = db.run(Hoard.filter(hoard => hoard.userId === userid && hoard.hoardtype == hoardType).result)
        matches.flatMap{ hoardRows => 
            //returns all relevant hoard data if hoard is unlocked (designated by final bool)
            if(hoardRows.head.unlocked) {
                println(hoardRows.head.unlocked)
                Future.successful((hoardRows.head.hoardId, hoardRows.head.cost, hoardRows.head.hoardlevel, 
                                   hoardRows.head.hoarditems, hoardRows.head.productionspeed, hoardRows.head.goldconversionrate,
                                   true))
            } 
            //returns all 0 and unlocked=false if not unlocked
            //shouldn't ever happen I think
            else {
                println(hoardRows.head.unlocked)
                Future.successful((0,0,0,0,0,0,false))
            }
        }

        
    }
    def getStealingInfo(username:String):Future[String] = Future.successful("hi")
    def loadUserInfo(username:String, userid:Int, info:String):Future[Int] = Future.successful(1)
    def loadHoardInfo(username:String, userid:Int, info:String):Future[Int] = Future.successful(1)
    def loadStealingInfo(username:String, userid:Int, info:String):Future[Int] = Future.successful(1)
    def stealFromUser(username:String, stolen:String):Future[Int] = Future.successful(1)
    def resetAll(userid:Int):Future[Boolean] = Future.successful(true)


    /**TO DO:
     * 
     * Test validateUser
     * Finish createUser
     *   -- includes coming up with universal upgrades
     * Test createUser
     * Verify getUserInfo with Ren
     * Test getHoardInfo
     * Implement all other unimplemented functions
     * 
     */
}
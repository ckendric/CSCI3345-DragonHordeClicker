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

    //hoard-specific upgrades
//these descriptions might not even need to be in here
    private val rocksAndMineralsDesc = List[String]("You discover pretty rocks, and decide to start collecting.", 
                                                    "Buy some shovels and picks to make the collection process faster.", 
                                                    "Tumblers make your rocks shiny, smooth, and pretty. They are more valuable!", 
                                                    "You buy more tools, including some power tools!", 
                                                    "Big machinery makes collecting even more efficient.",
                                                    "You purchase a mine for maximum rock and mineral collection.")
    private val junkFoodDesc = List[String]("Trash food tastes better than rocks.", 
                                            "You start pillaging houses for junk food instead of finding it in the wild.", 
                                            "You read up on some marketing techniques to improve your sales.", 
                                            "You begin to stake out stores to get bulk packages.", 
                                            "Stealing from factories is even more efficient than stealing from stores.", 
                                            "You purchase your own junk food factory.")
    private val ninetiesParaphernaliaDesc = List[String]("Bright, eye-searing colors attract you.", 
                                                         "", 
                                                         "For some reason, “90s kids” nostalgia makes a resurgence.", 
                                                         "", 
                                                         "", 
                                                         "")
    private val yarnDesc = List[String]("You discover the joy found in a soft bed of yarn.", 
                                        "Adopt a sheep!", 
                                        "You start dying your yarn pretty colors, and it’s value increases.", 
                                        "Adopt two more sheep. They’re terrified of your fire. ", 
                                        "Learn advanced shearing techniques. You spin yarn faster.", 
                                        "Purchase an entire flock of sheep. You like to pat their fluffy heads.")
    private val dtuffedAnimalsDesc = List[String]("You learn to knit with all of that yarn you have. Stuffed animals are adorable, even if all of yours look kind of wonky.", 
                                                  "You purchase an array of patterns, and they start looking better.", 
                                                  "You start focusing your marketing strategy to small children. Your sales increase.", 
                                                  "You learn better stuffing techniques, and stop accidentally creating holes with your claws.", 
                                                  "You now learn to crochet, and the stuffed-animal world is your oyster.", 
                                                  "You learn from grandmothers. Their secrets make you a master of stuffed animal knitting and crocheting techniques.")
    private val catsDesc = List[String]("Oh my god. It’s so adorable.", 
                                        "Having more cats means having more kittens, you discover. Every single one is purr-fect.", 
                                        "Your adopt-a-kitten campaign takes off!", 
                                        "You start baiting cats with fish. They paw-sitively flock to you.", 
                                        "Learn new petting techniques to attract more cats. Your cats are always feline good.", 
                                        "Catnip makes cat-napping easier. You don’t worry about the morals of this.")
    private val musicBoxesDesc = List[String]("The music is so pretty...", 
                                              "You begin learning how to make music boxes of your own.", 
                                              "You discover a market for collector’s music boxes.", 
                                              "Learn techniques for touching up old music boxes to restore broken ones.", 
                                              "Bionic ears make finding music boxes easier.", 
                                              "You purchase a factory to build music boxes. How industrious of you!")
    private val codingTextBooksDesc = List[String]("You’re introduced to the art of programming using Scala.", 
                                                   "There are languages other than Scala?", 
                                                   "When you stop drawing in your textbooks, they end up being worth more.", 
                                                   "You start finding textbooks in the REALLY obscure languages. You’ll probably never use them.", 
                                                   "Buy a printing press. Print more coding textbooks.", 
                                                   "Contract professors to write you new coding textbooks.")
        
    private val hoardUpgradeCosts = List[Int](25, 300, 4000, 7000, 20000, 100000)
        //might be a double
    private val hoardUpgradeNewSpeeds = List[Double](1, 4, 4, 7, 12, 20)
    private val hoardUpgradeGoldMultipliers = List[Double](1, 1, 2, 1, 1, 1)


    //universal upgrades

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

        //initialises user if it does not already exist
        matches.flatMap{ userRows => 
            if(userRows.nonEmpty) {
                Future.successful(false)
            } else {
                db.run(Users += UsersRow(-1, username, BCrypt.hashpw(password, BCrypt.gensalt()), 1))
                    //.map(addCount => addCount>0)
            }
        }
        val userId = db.run((for {user <- Users if user.username === username} yield {user.id}).result)

        //initialises all hoards for the user
        var i = 0
        var unlocked = true
        for(i <- 1 to 9) {
            if(i > 1) unlocked = false
            userId.flatMap { ids =>
                db.run(Hoard += HoardRow(-1, ids.head, 
                        /*hoardType*/i, 
                        /*cost*/cost(i-1), 
                        /*HoardLevel*/0, 
                        /*HoardItems*/0, 
                        /*ProductionSpeed*/0, 
                        /*Gold Conversion*/conversionRate(i-1),
                        /*unlocked*/unlocked))
            }
        }

        //initialises all hoard-specific upgrades
        var j = 0;
        i = 0;
        val hoards = userId.flatMap { ids => db.run(Hoard.filter(hoardRow => hoardRow.userId === ids.head).result)}
        for(i <- 0 to 8){
            for(j <- 1 to 6){
                hoards.flatMap { hoards =>
                    db.run(Hoardupgrade += HoardupgradeRow(-1, 
                                                       /*hoardId*/hoards(i).hoardId, 
                                                       /*upgradeNo*/j, 
                                                       /*cost*/hoardUpgradeCosts(j),
                                                       /*unlocked*/false,
                                                       /*newspeed*/hoardUpgradeNewSpeeds(j),
                                                       /*goldmultiplier*/hoardUpgradeGoldMultipliers(j)))
                }
            }
        }

        //initialises all universal upgrades
        for(i <- 0 to 4){
            userId.flatMap { ids =>
                db.run(Univupgrades += UnivupgradesRow(-1,ids.head,i,false))
            }
        }

        Future.successful(true)
    }

    def getUserInfo(userid:Int):Future[(Option[Int], Seq[Boolean])] =
    {
        //get basic user data
            //currently an option, could not be
        var gold = db.run((for {user <- Users if user.id === userid} yield {user.gold}).result).map(userRows => userRows.headOption)
        //unlocked universal upgrades
        var upgrades = db.run((for {uUpgrade <- Univupgrades if uUpgrade.userId == userid} yield {uUpgrade.unlocked}).result)
        for{ 
            g <- gold
            u <- upgrades
         } yield {
            (g, u)
         }
    }

    def getAllHordesInfo(userid:Int):Future[Seq[Boolean]] =
    {
        //unlocked hoards
        var hoards = db.run((for {hoard <- Hoard if hoard.userId == userid} yield {hoard.unlocked}).result)
        for{ h <- hoards } yield { h }
    }
    
    //returns: Future[(Int, Int, Int, Double, Double, Double, Boolean)]
    //    get all information for one of the user's hoards when said hoard is selected
    def getHoardInfo(userid:Int, hoardType:Int):Future[(Int, Int, Int, Double, Double, Double, Boolean)] = {
        //reult of all hoards with userid and hoardType
        val matches = db.run(Hoard.filter(hoard => hoard.userId === userid && hoard.hoardtype == hoardType).result)
        matches.flatMap{ hoardRows => 
            //returns all relevant hoard data if hoard is unlocked (designated by final bool)
            if(hoardRows.head.unlocked) {
                //println(hoardRows.head.unlocked)
                Future.successful((hoardRows.head.hoardId, hoardRows.head.cost, hoardRows.head.hoardlevel, 
                                   hoardRows.head.hoarditems, hoardRows.head.productionspeed, hoardRows.head.goldconversionrate,
                                   true))
            } 
            //returns all 0 and unlocked=false if not unlocked
            //shouldn't ever happen I think
            else {
                //println(hoardRows.head.unlocked)
                Future.successful((0,0,0,0,0,0,false))
            }
        }

        
    }

    //returns: Future[(Seq[Int],Seq[String])]
    //         a list of other userids and usernames, supposedly to be stolen from
    //I think this may need to be changed later on, but the rest might be handled in stealFromUser
    def getStealingInfo(username:String):Future[(Seq[Int], Seq[String])] = {

        //var victims = db.run((for {user <- Users if user.userId == userid} yield {hoard.unlocked}).result)
        var victimIds = db.run((for {user <- Users if !(user.username === username)} yield {user.id}).result)
        var victimUsns = db.run((for {user <- Users if !(user.username === username)} yield {user.username}).result)
        for{ 
            i <- victimIds
            u <- victimUsns
         } yield {
            (i,u)
         }
    }

    
    def loadUserInfo(username:String, userid:Int, info:String):Future[Int] = Future.successful(1)
    def loadHoardInfo(username:String, userid:Int, info:String):Future[Int] = Future.successful(1)
    def loadStealingInfo(username:String, userid:Int, info:String):Future[Int] = Future.successful(1)

    //randomly picks a hoard from a user and attempts to steal from them at a given probability
    /** Concerns:
      * 
      * - probability calculation: randomise how many items to be stolen
      * - gaussian random multiplied by how much in the hoard
      * - probably need to know what hoards the stealing user has (maybe I can do that?)
      * - reurn how much was stolen from which hoard
      * 
      */
    def stealFromUser(username:String, stolen:String):Future[Int] = Future.successful(1)

    //this is going to be the exact opposite of create user
    //need to figure out how to drop members of a db in code
    def resetAll(userid:Int):Future[Boolean] = Future.successful(true)


    /**TO DO:
     * 
     * Test validateUser
     * Finish createUser
     *   -- refactor universal upgrades
     * Test createUser
     * Verify getUserInfo with Ren
     * Test getUserInfo
     * Add HoardUpgrades to getHoardInfo
     * Test getHoardInfo
     * Test getStealingInfo
     * Implement all other unimplemented functions
     * 
     */
}
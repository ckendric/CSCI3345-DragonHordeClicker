package model

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import models.Tables._
import scala.concurrent.Future
import org.mindrot.jbcrypt.BCrypt //import mindrot in build.sbt
import com.typesafe.sslconfig.ssl.FakeChainedKeyStore.User

class HordeDatabaseModel(db: Database)(implicit ec: ExecutionContext) {
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
                    .map(addCount => addCount>0)
            }
        }
        //needs to initialise hoards for the user
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
    def getHoardInfo(userid:Int):Future[String] = Future.successful("hi")
    def getStealingInfo(username:String):Future[String] = Future.successful("hi")
    def loadUserInfo(username:String, userid:Int, info:String):Future[Int] = Future.successful(1)
    def loadHoardInfo(username:String, userid:Int, info:String):Future[Int] = Future.successful(1)
    def loadStealingInfo(username:String, userid:Int, info:String):Future[Int] = Future.successful(1)
    def stealFromUser(username:String, stolen:String):Future[Int] = Future.successful(1)
    def resetAll(userid:Int):Future[Boolean] = Future.successful(true)
}
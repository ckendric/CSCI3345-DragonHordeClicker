package model

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import model.Tables._
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
                db.run(Users += UsersRow(-1, username, BCrypt.hashpw(password, BCrypt.gensalt())))
                    .map(addCount => addCount>0)
            }
        }
    }

    def getUserInfo(userid:Int):Future[String] = Future.successful("hi")
    def getHoardInfo(userid:Int):Future[String] = Future.successful("hi")
    def getStealingInfo(username:String):Future[String] = Future.successful("hi")
    def loadUserInfo(username:String, userid:Int, info:String):Future[Int] = Future.successful(1)
    def loadHoardInfo(username:String, userid:Int, info:String):Future[Int] = Future.successful(1)
    def loadStealingInfo(username:String, userid:Int, info:String):Future[Int] = Future.successful(1)
    def stealFromUser(username:String, stolen:String):Future[Int] = Future.successful(1)
    def resetAll(userid:Int):Future[Boolean] = Future.successful(true)
}
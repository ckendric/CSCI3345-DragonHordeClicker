package controllers

import javax.inject._
import model._
import java.lang.ProcessBuilder.Redirect
import play.api.libs.json._

import models.ReadsAndWrites._
import play.api.mvc._
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.ExecutionContext
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

@Singleton
class Application @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents)(implicit ec: ExecutionContext) 
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {
    
  private val model = new HordeDatabaseModel(db)

  def index = Action { implicit request =>
    Ok(views.html.dragonHorde())
  }

  implicit val userDataReads = Json.reads[UserData]

 def validateUser = Action.async {implicit request => {
      request.body.asJson.map { body =>
        Json.fromJson[UserData](body) match {
          case JsSuccess(ud,path) =>
            (model.validateUser(ud.username,ud.password)).map { userExists =>
              userExists match {
                case Some(userid) =>
                  Ok(Json.toJson(true))
                    .withSession("username" -> ud.username, "userid" -> userid.toString, "csrfToken" -> play.filters.csrf.CSRF.getToken.get.value)
                case None => Ok(Json.toJson(false))
              }
            }
          case e @ JsError(_) => Future.successful(Redirect(routes.Application.index()))
        }
      }.getOrElse(Future.successful(Redirect(routes.Application.index())))
    }
  }

 def createUser = Action.async {implicit request => {
      request.body.asJson.map { body =>
        Json.fromJson[UserData](body) match {
          case JsSuccess(ud,path) =>
            (model.createUser(ud.username,ud.password)).map { userExists =>
              userExists match {
                case Some(userid) =>
                  Ok(Json.toJson(true))
                    .withSession("username" -> ud.username, "userid" -> userid.toString, "csrfToken" -> play.filters.csrf.CSRF.getToken.get.value)
                case None => Ok(Json.toJson(false))
              }
            }
          case e @ JsError(_) => Future.successful(Redirect(routes.Application.index()))
        }
      }.getOrElse(Future.successful(Redirect(routes.Application.index())))
    }
  }

  def createUserHoards = Action.async { implicit request => {
      val userIdOption = request.session.get("userid").map(userid => userid.toInt)
      val emptyInfo = false
      userIdOption.map { userid =>
        model.createUserHoards(userid).map(info => Ok(Json.toJson(info)))
      }.getOrElse(Future.successful(Ok(Json.toJson(emptyInfo))))
    }
  }

  def createUserHoardUpgrades = Action.async { implicit request => {
      val userIdOption = request.session.get("userid").map(userid => userid.toInt)
      val emptyInfo = false 
      userIdOption.map { userid =>
        model.createUserHoardUpgrades(userid).map(info => Ok(Json.toJson(info)))
      }.getOrElse(Future.successful(Ok(Json.toJson(emptyInfo))))
    }
  }

  def createUniversalUpgrades = Action.async { implicit request => {
      val userIdOption = request.session.get("userid").map(userid => userid.toInt)
      val emptyInfo = false
      userIdOption.map { userid =>
        model.createUniversalUpgrades(userid).map(info => Ok(Json.toJson(info)))
      }.getOrElse(Future.successful(Ok(Json.toJson(emptyInfo))))
    }
  }

  //(Option[Int], Seq[Boolean])
  def getUserInfo = Action.async { implicit request => {
      val userIdOption = request.session.get("userid").map(userid => userid.toInt)
      val emptyInfo = (None,Seq[Boolean]()) //need to know what type that userinfo is
      userIdOption.map { userid =>
        model.getUserInfo(userid).map(info => Ok(Json.toJson(info)))
      }.getOrElse(Future.successful(Ok(Json.toJson(emptyInfo))))
    }
  }
  


  def getHoardInfo = Action.async { implicit request => {
      val userIdOption = request.session.get("userid").map(userid => userid.toInt)
      val emptyInfo = (0,0,0,0.0,0.0,0.0,false)
      val emptyInfo2 = (0,0,0,0.0,0.0,1.0,false)
      val emptyInfo3 = (0,0,0,0.0,0.0,2.0,false)
      userIdOption.map { userid =>
        request.body.asJson.map { body =>
          Json.fromJson[Int](body) match { //info will not be a string; this will change a lot
            case JsSuccess(info,path) =>
              model.getHoardInfo(userid, info).map(ret => Ok(Json.toJson(ret)))
            case e @ JsError(_) => Future.successful(Ok(Json.toJson(emptyInfo)))
          }
        }.getOrElse(Future.successful(Ok(Json.toJson(emptyInfo2))))
      }.getOrElse(Future.successful(Ok(Json.toJson(emptyInfo3))))
    }
  }

  def getHoardUpgradesInfo = Action.async { implicit request => {
      val userIdOption = request.session.get("userid").map(userid => userid.toInt)
      val emptyInfo = Seq[(Int, Int, Int, Boolean, Double, Double)]() //need to know what type that userinfo is
      userIdOption.map { userid =>
        request.body.asJson.map { body =>
          Json.fromJson[Int](body) match { //info will not be a string; this will change a lot
            case JsSuccess(info,path) =>
              model.getHoardUpgradesInfo(userid, info).map(ret => Ok(Json.toJson(ret)))
            case e @ JsError(_) => Future.successful(Ok(Json.toJson(emptyInfo)))
          }
        }.getOrElse(Future.successful(Ok(Json.toJson(emptyInfo))))
      }.getOrElse(Future.successful(Ok(Json.toJson(emptyInfo))))
    }
  }

  def getAllHoardsInfo = Action.async { implicit request => {
      val userIdOption = request.session.get("userid").map(userid => userid.toInt)
      val emptyInfo = Seq[Boolean]() //need to know what type that userinfo is
      userIdOption.map { userid =>
        model.getAllHordesInfo(userid).map(info => Ok(Json.toJson(info)))
      }.getOrElse(Future.successful(Ok(Json.toJson(emptyInfo))))
    }
  }

  def getStealingInfo = Action.async { implicit request => {
      val userIdOption = request.session.get("userid").map(userid => userid.toInt)
      val emptyInfo = Seq[(Int, String)]()//need to know what type that userinfo is
       userIdOption.map { userid =>
        model.getStealingInfo(userid).map(info => Ok(Json.toJson(info)))
      }.getOrElse(Future.successful(Ok(Json.toJson(emptyInfo))))
    }
  }

  def getGold = Action.async { implicit request => {
      val userIdOption = request.session.get("userid").map(userid => userid.toInt)
      val emptyInfo = None //need to know what type that userinfo is
      userIdOption.map { userid =>
        model.getGold(userid).map(info => Ok(Json.toJson(info)))
      }.getOrElse(Future.successful(Ok(Json.toJson(emptyInfo))))
    }
  }

  def loadUserInfo = Action.async { implicit request => {
      val userIdOption = request.session.get("userid").map(userid => userid.toInt)
      val usernameOption = request.session.get("username")
      userIdOption.map { userid =>
        request.body.asJson.map { body =>
          Json.fromJson[String](body) match { //info will not be a string; this will change a lot
            case JsSuccess(info,path) =>
              usernameOption.map{ username =>
                  model.loadUserInfo(username, userid, info).map(count => Ok(Json.toJson( count > 0 )))
              }.getOrElse(Future.successful(Ok(Json.toJson(false))))
            case e @ JsError(_) => Future.successful(Redirect(routes.Application.index()))
          }
        }.getOrElse(Future.successful(Ok(Json.toJson(false))))
      }.getOrElse(Future.successful(Ok(Json.toJson(false))))
    }
  }

  //id, cost, hordeLevel, itemStored, itemIncrement, goldConv, true
  //LoadHorde(typee: Int, cost: Int,level: Int, items: Double, productionSpeed: Double, goldConversion: Double,unlocked: Boolean)
  def loadHoardInfo = Action.async { implicit request => {
      val userIdOption = request.session.get("userid").map(userid => userid.toInt)
      userIdOption.map { userid =>
        request.body.asJson.map { body =>
          Json.fromJson[models.LoadHorde](body) match { //info will not be a string; this will change a lot
            case JsSuccess(info,path) =>
              model.loadHoardInfo(userid, info.typee, info.cost, info.level, info.items, info.productionSpeed,info.goldConversion, info.unlocked).map(count => Ok(Json.toJson( count > 0 )))
            case e @ JsError(_) => Future.successful(Redirect(routes.Application.index()))
          }
        }.getOrElse(Future.successful(Ok(Json.toJson(false))))
      }.getOrElse(Future.successful(Ok(Json.toJson(false))))
    }
  }
  
  def addNewHoard = Action.async { implicit request => {
    println("in app addNewHoard")
    val userIdOption = request.session.get("userid").map(userid => userid.toInt)
    userIdOption.map { userid =>
      request.body.asJson.map { body =>
        Json.fromJson[(Int,Boolean,Int)](body) match { //Int, Bool, Int
          case JsSuccess((hoardtype,unlocked,newgold),path) =>{
            println("in JSSuccess addNewHoard")
            model.unlockNewHoard(userid, hoardtype, unlocked, newgold).map(count => Ok(Json.toJson( count > 0 )))}
          case e @ JsError(_) => Future.successful(Redirect(routes.Application.index()))
        }
      }.getOrElse(Future.successful(Ok(Json.toJson(false))))
    }.getOrElse(Future.successful(Ok(Json.toJson(false))))
  }
  }

  def levelUpHoard = Action.async { implicit request => {
    println("in app levelupHoard")
    val userIdOption = request.session.get("userid").map(userid => userid.toInt)
    userIdOption.map { userid =>
      request.body.asJson.map { body =>
        Json.fromJson[models.LevelUpData](body) match { //Int, Bool, Int
          case JsSuccess(data,path) =>{
            println("in JSSuccess levelupHoard")
            model.levelUpHoard(userid, data.id, data.level, data.productionSpeed,data.cost,data.gold).map(count => Ok(Json.toJson( count > 0 )))}
          case e @ JsError(_) => Future.successful(Redirect(routes.Application.index()))
        }
      }.getOrElse(Future.successful(Ok(Json.toJson(false))))
    }.getOrElse(Future.successful(Ok(Json.toJson(false))))
  }}

  def upgradeHoard = Action.async { implicit request => {
    val userIdOption = request.session.get("userid").map(userid => userid.toInt)
    userIdOption.map { userid =>
      request.body.asJson.map { body =>
        Json.fromJson[models.UpgradeHorde](body) match { //Int, Bool, Int
          case JsSuccess(data,path) =>{
            println("in JSSuccess upgradeHoard")
            model.upgradeHoard(userid, data.hordeId,data.productionSpeed,data.goldConversion,data.upgradeId,data.upgradeBool).map(count => Ok(Json.toJson( count > 0 )))}
          case e @ JsError(_) => Future.successful(Redirect(routes.Application.index()))
        }
      }.getOrElse(Future.successful(Ok(Json.toJson(false))))
    }.getOrElse(Future.successful(Ok(Json.toJson(false))))
  }}

  //(userid:Int, upgradeId:Int):Future[(Int, Int, Int, Boolean, Double, Double)] 
  def getOneHoardUpgradeInfo = Action.async { implicit request => {
    val blankInfo = (0,0,0, false, 0.0, 0.0)
    val userIdOption = request.session.get("userid").map(userid => userid.toInt)
    userIdOption.map { userid =>
      request.body.asJson.map { body =>
        Json.fromJson[Int](body) match { //Int, Bool, Int
          case JsSuccess(data,path) =>{
            println("in JSSuccess getOneHoardUpgradeInfo")
            model.getOneHoardUpgradeInfo(userid, data).map(ret => Ok(Json.toJson(ret)))}
          case e @ JsError(_) => Future.successful(Ok(Json.toJson(blankInfo)))
        }
      }.getOrElse(Future.successful(Ok(Json.toJson(blankInfo))))
    }.getOrElse(Future.successful(Ok(Json.toJson(blankInfo))))
  }}

  def stealFromUser = Action.async { implicit request => {
      val userIdOption = request.session.get("userid").map(userid => userid.toInt)
      val usernameOption = request.session.get("username")
      userIdOption.map { userid =>
        request.body.asJson.map { body =>
          Json.fromJson[Int](body) match { //info will not be a string; this will change a lot
            case JsSuccess(info,path) =>
              usernameOption.map{ username =>
                  model.stealFromUser(userid, username, info).map(result => Ok(Json.toJson(result)))
              }.getOrElse(Future.successful(Ok(Json.toJson(("",0)))))
            case e @ JsError(_) => Future.successful(Redirect(routes.Application.index()))
          }
        }.getOrElse(Future.successful(Ok(Json.toJson(("",0)))))
      }.getOrElse(Future.successful(Ok(Json.toJson(("",0)))))
    }
  }

  def addGold = Action.async { implicit request => {
      val userIdOption = request.session.get("userid").map(userid => userid.toInt)
      val usernameOption = request.session.get("username")
      userIdOption.map { userid =>
        request.body.asJson.map { body =>
          Json.fromJson[Int](body) match { //info will not be a string; this will change a lot
            case JsSuccess(info,path) =>
              usernameOption.map{ username =>
                  model.addGold(userid, username, info).map(count => Ok(Json.toJson( count > 0 )))
              }.getOrElse(Future.successful(Ok(Json.toJson(false))))
            case e @ JsError(_) => Future.successful(Redirect(routes.Application.index()))
          }
        }.getOrElse(Future.successful(Ok(Json.toJson(false))))
      }.getOrElse(Future.successful(Ok(Json.toJson(false))))
    }
  }

  def resetUser = Action.async { implicit request => {
    val userIdOption = request.session.get("userid").map(userid => userid.toInt)
    userIdOption.map { userid =>
      model.resetAll(userid).map(info => Ok(Json.toJson(info)))
    }.getOrElse(Future.successful(Ok(Json.toJson(false))))
  }}

  def logout = Action { implicit request => {
    println("App logout")
    //Redirect(routes.Application.index()).withNewSession
    Ok(Json.toJson(true)).withSession(request.session-"username"-"userid")
    }
  }

}

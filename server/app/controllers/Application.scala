package controllers

import javax.inject._
import model._
import java.lang.ProcessBuilder.Redirect
import play.api.libs.json._

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
            (model.createUser(ud.username,ud.password)).map { userCreated =>
              if (userCreated){
                Ok(Json.toJson(true))
                  .withSession("username" -> ud.username, "csrfToken" -> play.filters.csrf.CSRF.getToken.get.value)
              } else {
                Ok(Json.toJson(false))
              }
            }
          case e @ JsError(_) => Future.successful(Redirect(routes.Application.index()))
        }
      }.getOrElse(Future.successful(Redirect(routes.Application.index())))
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
      userIdOption.map { userid =>
        request.body.asJson.map { body =>
          Json.fromJson[Int](body) match { //info will not be a string; this will change a lot
            case JsSuccess(info,path) =>
              model.getHoardInfo(userid, info).map(ret => Ok(Json.toJson(ret)))
            case e @ JsError(_) => Future.successful(Ok(Json.toJson(emptyInfo)))
          }
        }.getOrElse(Future.successful(Ok(Json.toJson(emptyInfo))))
      }.getOrElse(Future.successful(Ok(Json.toJson(emptyInfo))))
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
      val emptyInfo = (Seq[Int](), Seq[String]())//need to know what type that userinfo is
       userIdOption.map { userid =>
        model.getStealingInfo(userid).map(info => Ok(Json.toJson(info)))
      }.getOrElse(Future.successful(Ok(Json.toJson(emptyInfo))))
    }
  }

  def getGold = Action.async { implicit request => {
      val userIdOption = request.session.get("userid").map(userid => userid.toInt)
      val emptyInfo = 0 //need to know what type that userinfo is
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

  def loadHoardInfo = Action.async { implicit request => {
      val userIdOption = request.session.get("userid").map(userid => userid.toInt)
      val usernameOption = request.session.get("username")
      userIdOption.map { userid =>
        request.body.asJson.map { body =>
          Json.fromJson[Int](body) match { //info will not be a string; this will change a lot
            case JsSuccess(info,path) =>
              usernameOption.map{ username =>
                  model.loadHoardInfo(username, userid, info).map(count => Ok(Json.toJson( count > 0 )))
              }.getOrElse(Future.successful(Ok(Json.toJson(false))))
            case e @ JsError(_) => Future.successful(Redirect(routes.Application.index()))
          }
        }.getOrElse(Future.successful(Ok(Json.toJson(false))))
      }.getOrElse(Future.successful(Ok(Json.toJson(false))))
    }
  }

  def stealFromUser = Action.async { implicit request => {
      val userIdOption = request.session.get("userid").map(userid => userid.toInt)
      val usernameOption = request.session.get("username")
      userIdOption.map { userid =>
        request.body.asJson.map { body =>
          Json.fromJson[String](body) match { //info will not be a string; this will change a lot
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

  def logout = Action {
    Redirect(routes.Application.index()).withNewSession
  }

  def dragonHordeLoginDemo = Action {
    Ok(views.html.dragonHordeLoginDemo())
  }

  def dragonHordeDemoPage = Action {
    val user = "mlewis"
    val icon = "images/demoIcon.jpg"
    Ok{views.html.dragonHordeDemoPage()}
  }

}

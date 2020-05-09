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
/*
  def login = Action { implicit request =>
    Ok(views.html.dragonHordeLoginDemo())
  }
*/

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

  //(Option[Int], Seq[Boolean], Seq[Boolean])
  def getUserInfo = Action.async { implicit request => {
      val userIdOption = request.session.get("userid").map(userid => userid.toInt)
      val emptyInfo = "" //need to know what type that userinfo is
      userIdOption.map { userid =>
        model.getUserInfo(userid).map(info => Ok(Json.toJson(info)))
      }.getOrElse(Future.successful(Ok(Json.toJson(emptyInfo))))
    }
  }

  //(Int, Int, Int, Double, Double, Double, Boolean)
  def getHoardInfo = Action.async { implicit request => {
      val userIdOption = request.session.get("userid").map(userid => userid.toInt)
      val emptyInfo = "" //need to know what type that userinfo is
      val hoardNumber = 0 //need to know which hoard's info is being requested
      userIdOption.map { userid =>
        model.getHoardInfo(userid, hoardNumber).map(info => Ok(Json.toJson(info)))
      }.getOrElse(Future.successful(Ok(Json.toJson(emptyInfo))))
    }
  }

  def getStealingInfo = Action.async { implicit request => {
      val usernameOption = request.session.get("username")
      val emptyInfo = "" //need to know what type that userinfo is
      usernameOption.map { username =>
        //requesting to be passed userid pls n thenk u -Quentin
        model.getStealingInfo(username).map(info => Ok(Json.toJson(info)))
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
          Json.fromJson[String](body) match { //info will not be a string; this will change a lot
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

  def loadStealingInfo = Action.async { implicit request => {
      val userIdOption = request.session.get("userid").map(userid => userid.toInt)
      val usernameOption = request.session.get("username")
      userIdOption.map { userid =>
        request.body.asJson.map { body =>
          Json.fromJson[String](body) match { //info will not be a string; this will change a lot
            case JsSuccess(info,path) =>
              usernameOption.map{ username =>
                  model.loadStealingInfo(username, userid, info).map(count => Ok(Json.toJson( count > 0 )))
              }.getOrElse(Future.successful(Ok(Json.toJson(false))))
            case e @ JsError(_) => Future.successful(Redirect(routes.Application.index()))
          }
        }.getOrElse(Future.successful(Ok(Json.toJson(false))))
      }.getOrElse(Future.successful(Ok(Json.toJson(false))))
    }
  }

  def stealFromUser = Action.async { implicit request => {
    val usernameOption = request.session.get("username")
    usernameOption.map { username =>
      request.body.asJson.map { body =>
        Json.fromJson[String](body) match {
          case JsSuccess(stealUser, path) => //might change what steal info is
            model.stealFromUser(username,stealUser).map(count => Ok(Json.toJson(count > 0)))
          case e @ JsError(_) => Future.successful(Redirect(routes.Application.index()))
        }
      }.getOrElse(Future.successful(Ok(Json.toJson(false))))
    }.getOrElse(Future.successful(Ok(Json.toJson(false))))
  }}

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

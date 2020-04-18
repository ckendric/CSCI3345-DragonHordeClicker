package controllers

import javax.inject._
import model._
import java.lang.ProcessBuilder.Redirect

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
    Ok(views.html.dragonHordeLoginDemo())
  }

  def login = Action { implicit request =>
    Ok(views.html.dragonHordeLoginDemo())
  }

  def validateUser = Action.async { implicit request =>
    val postVals = request.body.asFormUrlEncoded
    postVals.map { args =>
      val username = args("username").head
      val password = args("password").head
      (model.validateUser(username,password)).map { userExists =>
        userExists match {
          case Some(userid) =>
            Ok(views.html.dragonHordeDemoPage())
              .withSession("username" -> username, "userid" -> userid.toString, "csrfToken" -> play.filters.csrf.CSRF.getToken.get.value)
          case None => Redirect(routes.Application.login())
        }
      }
    }.getOrElse(Future.successful(Redirect(routes.Application.login())))
  }

  def createUser = Action.async { implicit request =>
    val postVals = request.body.asFormUrlEncoded
    postVals.map { args =>
      val username = args("username").head
      val password = args("password").head
      model.createUser(username,password).map { userCreated =>
        if (userCreated){
          Ok(views.html.dragonHordeDemoPage())
              .withSession("username" -> username, "csrfToken" -> play.filters.csrf.CSRF.getToken.get.value)
        } else {
          Redirect(routes.Application.login())
        }
      }
    }.getOrElse(Future.successful(Redirect(routes.Application.login())))
  }

// Load user info
// Load hoards info
// Load stealing info
// Load gold
// Steal from user
// Add to hoard
// Add new hoard
// Save to database
// Reset

  def logout = Action {
    Redirect(routes.Application.login()).withNewSession
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

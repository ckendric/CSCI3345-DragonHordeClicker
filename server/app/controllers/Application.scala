package controllers

import javax.inject._

import play.api.mvc._

@Singleton
class Application @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index = Action {
    Ok(views.html.index("It works!"))
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

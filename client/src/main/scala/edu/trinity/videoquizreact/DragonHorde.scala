package edu.trinity.videoquizreact

import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.html
import org.scalajs.dom.experimental.Headers
import org.scalajs.dom.experimental.Fetch
import org.scalajs.dom.experimental.HttpMethod
import org.scalajs.dom.experimental.RequestMode
import org.scalajs.dom.experimental.RequestInit
import play.api.libs.json.Json
import scala.scalajs.js.Thenable.Implicits._
import play.api.libs.json.JsError
import scala.concurrent.ExecutionContext
import play.api.libs.json.JsSuccess
import scala.scalajs.js.annotation.JSExportTopLevel
import scalajs.js

object DragonHorde {

    implicit val ex = ExecutionContext.global

    val csrfToken = document.getElementById("csrfToken").asInstanceOf[html.Input].value
    val createRoute = document.getElementById("createRoute").asInstanceOf[html.Input].value
    val validateRoute = document.getElementById("validateRoute").asInstanceOf[html.Input].value

    val hoardRoute = document.getElementById("hoardRoute").asInstanceOf[html.Input].value
    val addToHoardRoute = document.getElementById("addToHoardRoute").asInstanceOf[html.Input].value
    val addNewHoardRoute = document.getElementById("addNewHoardRoute").asInstanceOf[html.Input].value
    
    val upgradeHoardRoute = document.getElementById("upgradeHoardRoute").asInstanceOf[html.Input].value
    val upgradeEverythingRoute = document.getElementById("upgradeEverythingRoute").asInstanceOf[html.Input].value

    val goldRoute = document.getElementById("goldRoute").asInstanceOf[html.Input].value
    
    val userInfoRoute = document.getElementById("userInfoRoute").asInstanceOf[html.Input].value
    val stealRoute = document.getElementById("stealRoute").asInstanceOf[html.Input].value
    val resetRoute = document.getElementById("resetRoute").asInstanceOf[html.Input].value

    def init(): Unit = {
        println("initializing scala.js")
    }

    @JSExportTopLevel("login")
    def login(): Unit = {
        println("logging in scalajs... commented out")
    //    val username = document.getElementById("loginName").asInstanceOf[html.Input].value
    //    val password = document.getElementById("loginPass").asInstanceOf[html.Input].value
    //    val data = models.UserData(username,password)

    //    FetchJson.fetchPost(validateRoute,csrfToken, data, (bool:Boolean) => {
    //         if (bool) {            
    //            document.getElementById("login-section").asInstanceOf[js.Dynamic].hidden = true
    //            document.getElementById("message-section").asInstanceOf[js.Dynamic].hidden = false
    //            document.getElementById("login-message").innerHTML = ""
    //            document.getElementById("create-message").innerHTML = ""
    //            }
    //            else {
    //                document.getElementById("login-message").innerHTML = "Login Failed"
    //            }
    //    }, e => {
    //        println("Fetch error: " + e)
    //    })
    }

    @JSExportTopLevel("createUser")
    def createUser(): Unit = {
    //    val username = document.getElementById("createName").asInstanceOf[html.Input].value
    //    val password = document.getElementById("createPass").asInstanceOf[html.Input].value
    //    val data = models.UserData(username, password)
    //    FetchJson.fetchPost(createRoute, csrfToken, data, (bool: Boolean) => {
    //    if(bool) {
    //        document.getElementById("login-section").asInstanceOf[js.Dynamic].hidden = true
    //        document.getElementById("message-section").asInstanceOf[js.Dynamic].hidden = false
    //        document.getElementById("login-message").innerHTML = ""
    //        document.getElementById("create-message").innerHTML = ""
    //        document.getElementById("createName").asInstanceOf[html.Input].value = ""
    //        document.getElementById("createPass").asInstanceOf[html.Input].value = ""
    //        loadGlobalMessages()
    //    } else {
    //        document.getElementById("create-message").innerHTML = "User Creation Failed"
    //    }
    //}, e => {
    //  println("Fetch error: " + e)
    //})
    println("creating user scalajs... commented out")
  }

  @JSExportTopLevel("loadUserInfo")
  def loadUserInfo(): Unit = {
    println("loading user info scalajs... Christine has not implemented this yet")
    loadHoardsInfo()
    loadStealingInfo()
    loadGold()
  }

  def loadHoardsInfo(): Unit = {
    println("loading hoards info scalajs... Christine has not implemented this yet")
  }  

  def loadStealingInfo(): Unit = {
      println("loading stealing info scalajs... Christine has not implemented this yet")
  }

  def loadGold(): Unit = {
    println("loading gold scalajs... Christine has not implemented this yet")
  }

  @JSExportTopLevel("stealFromUser")
  def stealFromUser(): Unit = {
      println("stealing from user scalajs... Christine has not implemented this yet")
  }

  @JSExportTopLevel("addToHoard")
  def addToHoard(): Unit = {
      println("adding to hoard scalajs... Christine has not implemented this yet")
  }

  @JSExportTopLevel("upgradeHoard")
  def upgradeHoard(): Unit = {
      println("upgrading hoard scalajs... Christine has not implemented this yet")
  }

  @JSExportTopLevel("upgradeEverything")
  def upgradeEverything(): Unit = {
      println("upgrading everything scalajs... Christine has yet to implement this")
  }

  @JSExportTopLevel("reset")
  def reset(): Unit = {
      println("resetting scalajs... Christine has yet to implement this")
  }

}
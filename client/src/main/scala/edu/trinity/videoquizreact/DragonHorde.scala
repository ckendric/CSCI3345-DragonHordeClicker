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
import models.ReadsAndWrites._

object DragonHorde {

    implicit val ex = ExecutionContext.global

    val csrfToken = document.getElementById("csrfToken").asInstanceOf[html.Input].value
    val createRoute = document.getElementById("createRoute").asInstanceOf[html.Input].value
    val validateRoute = document.getElementById("validateRoute").asInstanceOf[html.Input].value
    val logoutRoute = document.getElementById("logoutRoute").asInstanceOf[html.Input].value


    val hoardRoute = document.getElementById("hoardRoute").asInstanceOf[html.Input].value
    val addToHordeRoute = document.getElementById("addToHordeRoute").asInstanceOf[html.Input].value
    val addNewHordeRoute = document.getElementById("addNewHordeRoute").asInstanceOf[html.Input].value
    
    val upgradeHordeRoute = document.getElementById("upgradeHordeRoute").asInstanceOf[html.Input].value
    val upgradeEverythingRoute = document.getElementById("upgradeEverythingRoute").asInstanceOf[html.Input].value

    val goldRoute = document.getElementById("goldRoute").asInstanceOf[html.Input].value
    
    val userInfoRoute = document.getElementById("userInfoRoute").asInstanceOf[html.Input].value
    val loadHordeRoute = document.getElementById("loadHordeRoute").asInstanceOf[html.Input].value
    val loadStealingInfoRoute = document.getElementById("loadStealingInfoRoute").asInstanceOf[html.Input].value
    val loadGoldRoute = document.getElementById("loadGoldRoute").asInstanceOf[html.Input].value

    val stealRoute = document.getElementById("stealRoute").asInstanceOf[html.Input].value
    val resetRoute = document.getElementById("resetRoute").asInstanceOf[html.Input].value



    def init(): Unit = {
        println("initializing scala.js")
    }

    @JSExportTopLevel("login")
    def login(): Unit = {
        println("logging in scalajs")
        val username = document.getElementById("loginName").asInstanceOf[html.Input].value
        val password = document.getElementById("loginPass").asInstanceOf[html.Input].value
        val data = models.UserData(username,password)

        FetchJson.fetchPost(validateRoute,csrfToken, data, (bool:Boolean) => {
             if (bool) {            
                document.getElementById("login-section").asInstanceOf[js.Dynamic].hidden = true
                document.getElementById("horde-section").asInstanceOf[js.Dynamic].hidden = false
                document.getElementById("login-message").innerHTML = ""
                document.getElementById("create-message").innerHTML = ""
                loadUserInfo()
                }
                else {
                    document.getElementById("login-message").innerHTML = "Login Failed"
                }
        }, e => {
            println("Fetch error: " + e)
        })
    }

    @JSExportTopLevel("createUser")
    def createUser(): Unit = {
      println("creating user scalajs")
        val username = document.getElementById("createName").asInstanceOf[html.Input].value
        val password = document.getElementById("createPass").asInstanceOf[html.Input].value
        val data = models.UserData(username, password)
        FetchJson.fetchPost(createRoute, csrfToken, data, (bool: Boolean) => {
        if(bool) {
            document.getElementById("login-section").asInstanceOf[js.Dynamic].hidden = true
            document.getElementById("horde-section").asInstanceOf[js.Dynamic].hidden = false
            document.getElementById("login-message").innerHTML = ""
            document.getElementById("create-message").innerHTML = ""
            document.getElementById("createName").asInstanceOf[html.Input].value = ""
            document.getElementById("createPass").asInstanceOf[html.Input].value = ""
            loadUserInfo()
        } else {
            document.getElementById("create-message").innerHTML = "User Creation Failed"
        }
    }, e => {
      println("Fetch error: " + e)
    })
  }

  @JSExportTopLevel("logout")
  def logout():Unit = {
    FetchJson.fetchGet(logoutRoute, (bool:Boolean) => {
      document.getElementById("login-section").asInstanceOf[js.Dynamic].hidden = false
      document.getElementById("horde-section").asInstanceOf[js.Dynamic].hidden = true
    }, e=> {
      println("Fetch error " + e)
    })
  }

  @JSExportTopLevel("loadUserInfo")
  def loadUserInfo(): Unit = {
    println("loading user info scalajs")
    loadStealingInfo()
    loadGold()
    loadHordeInfo()
  }

  def loadHordeInfo(): Unit = {
    println("loading hoards info scalajs.")
    val ul = document.getElementById("horde-section")

    //this currently just has hordes as a list of strings. I think this is okay for now, 
    //but we should get together to see how exactly we want this

    FetchJson.fetchGet(loadHordeRoute, (hordes: List[String] ) => {
      for(horde <- hordes) {
        val li = document.createElement("li")
        val text = document.createTextNode(horde)
        li.appendChild(text)
        ul.appendChild(li)
      }
    }, e => {
      println("Fetch error: " + e)
    })
}  

  def loadStealingInfo(): Unit = {
    println("loading stealing info scalajs.")
    // Example from tasks 

    //this would currently just display the usernames of the vitcims (all users)
    val ul = document.getElementById("stealing-section")
        ul.innerHTML =""
        FetchJson.fetchGet(loadStealingInfoRoute, (victims:List[String]) => {
            for(victim <- victims) {
                       val li = document.createElement("li")
                        val text = document.createTextNode("steal from: " + victim)
                        li.appendChild(text)
                        ul.appendChild(li)
            }
        }, e => {
            println("Fetch error: " + e)
        })
  }

  def loadGold(): Unit = {
    println("loading gold scalajs.")
    val txt = document.getElementById("goldAmount")
    txt.innerHTML =""
    FetchJson.fetchGet(loadGoldRoute, (gold:Double) => {
      txt.innerHTML = gold.toString()
        }, e => {
            println("Fetch error: " + e)
        })
  }

  @JSExportTopLevel("stealFromUser")
  def stealFromUser(): Unit = {
      println("stealing from user scalajs")
    
        val username = document.getElementById("user").asInstanceOf[html.Input].value
        val victim = document.getElementById("victim").asInstanceOf[html.Input].value    
        val data = models.StealData(username, victim)
        FetchJson.fetchPost(stealRoute, csrfToken, data, (bool: Boolean) => {
        if(bool) {
            println("successfully stole from " + victim)
            loadUserInfo()
        } else {
            document.getElementById("create-message").innerHTML = "Stealing Failed"
        }
    }, e => {
      println("Fetch error: " + e)
    })
  }

  @JSExportTopLevel("addToHorde")
  def addToHorde(): Unit = {
      println("adding to hoard scalajs...")
      val username = document.getElementById("user").asInstanceOf[html.Input].value
      val horde = document.getElementById("hode").asInstanceOf[html.Input].value    
      val data = models.HordeData(username, horde)
      FetchJson.fetchPost(addToHordeRoute, csrfToken, data, (bool: Boolean) => {
         if(bool) {
            println("successfully added to " + horde)
            loadUserInfo()
        } else {
            document.getElementById("create-message").innerHTML = "Adding to horde Failed"
      }
    }, e => {
      println("Fetch error: " + e)
    })    
  }

  @JSExportTopLevel("upgradeHorde")
  def upgradeHorde(): Unit = {
      println("upgrading hoard scalajs...")
      val username = document.getElementById("user").asInstanceOf[html.Input].value
      val horde = document.getElementById("hode").asInstanceOf[html.Input].value    
      val data = models.HordeData(username, horde)
      FetchJson.fetchPost(upgradeHordeRoute, csrfToken, data, (bool: Boolean) => {
         if(bool) {
            println("successfully upgraded " + horde)
            loadUserInfo()
        } else {
            document.getElementById("create-message").innerHTML = "Upgrading Horde Failed"
      }
    }, e => {
      println("Fetch error: " + e)
    })   
  }

  @JSExportTopLevel("upgradeEverything")
  def upgradeEverything(): Unit = {
      println("upgrading everything scalajs...")
      val username = document.getElementById("user").asInstanceOf[html.Input].value
      val data = models.User(username)
      FetchJson.fetchPost(upgradeEverythingRoute, csrfToken, data, (bool: Boolean) => {
         if(bool) {
            println("successfully upgradded eveything")
            loadUserInfo()
        } else {
            document.getElementById("create-message").innerHTML = "User Creation Failed"
      }
    }, e => {
      println("Fetch error: " + e)
    })   
  }

  @JSExportTopLevel("reset")
  def reset(): Unit = {
      println("resetting scalajs... Christine has yet to implement this")
      val username = document.getElementById("user").asInstanceOf[html.Input].value
      val data = models.User(username)
      FetchJson.fetchPost(resetRoute, csrfToken, data, (bool: Boolean) => {
         if(bool) {
            println("successfully reset eveything")
            loadUserInfo()
        } else {
            document.getElementById("create-message").innerHTML = "Resetting Failed"
      }
    }, e => {
      println("Fetch error: " + e)
    })   
  }

}
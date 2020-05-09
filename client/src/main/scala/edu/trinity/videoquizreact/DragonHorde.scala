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
import java.util.concurrent._

object DragonHorde {

    implicit val ex = ExecutionContext.global

    val csrfToken = document.getElementById("csrfToken").asInstanceOf[html.Input].value
    val createRoute = document.getElementById("createRoute").asInstanceOf[html.Input].value
    val validateRoute = document.getElementById("validateRoute").asInstanceOf[html.Input].value
    val logoutRoute = document.getElementById("logoutRoute").asInstanceOf[html.Input].value


    val loadHordeRoute = document.getElementById("loadHordeRoute").asInstanceOf[html.Input].value
    val addNewHordeRoute = document.getElementById("addNewHordeRoute").asInstanceOf[html.Input].value
    val addGoldRoute = document.getElementById("addGoldRoute").asInstanceOf[html.Input].value
    
    val upgradeHordeRoute = document.getElementById("upgradeHordeRoute").asInstanceOf[html.Input].value
    val upgradeUniversalRoute = document.getElementById("upgradeUniversalRoute").asInstanceOf[html.Input].value

    val goldRoute = document.getElementById("goldRoute").asInstanceOf[html.Input].value
    
    val userInfoRoute = document.getElementById("userInfoRoute").asInstanceOf[html.Input].value
    val getAllHordesRoute = document.getElementById("getAllHordesRoute").asInstanceOf[html.Input].value
    val getHordeInfoRoute = document.getElementById("getHordeInfoRoute").asInstanceOf[html.Input].value
    val getStealingInfoRoute = document.getElementById("getStealingInfoRoute").asInstanceOf[html.Input].value
    val getGoldRoute = document.getElementById("getGoldRoute").asInstanceOf[html.Input].value
    val getHordeUpgradesRoute = document.getElementById("getHordeUpgradesRoute").asInstanceOf[html.Input].value
    val getUserInfoRoute = document.getElementById("getUserInfoRoute").asInstanceOf[html.Input].value

    val loadStealRoute = document.getElementById("loadStealRoute").asInstanceOf[html.Input].value
    val resetRoute = document.getElementById("resetRoute").asInstanceOf[html.Input].value




    var itemStored = 0
    var itemIncrement = 0
    var goldConv = 0
    var goldTotal = 0


    //delayed and timed updating to database... how to get this to start when people log in?
    //val ex = new ScheduledThreadPoolExecutor(1)
    //val autoUpdate = new Runnable { 
    //  def run() = (itemStored += itemIncrement*100) 
    //}
    //val f = ex.scheduleAtFixedRate(autoUpdate, 1, 1, TimeUnit.SECONDS)
    //f.cancel(false)

    //val exec = new ScheduledExecutorService(2)
    //val loadUpdate = new Runnable { 
    //  def run() = loadHordeData()
    //}
    //val t = exec.scheduleAtFixedRate(loadUpdate, 30, 30, TimeUnit.SECONDS)
    //t.cancel(false)




    case class HordeInfo(id: Int, cost:Int, level:Int, items: Double, productionSpeed: Double, goldConversion: Double)

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
                document.getElementById("login").asInstanceOf[js.Dynamic].hidden = true
                document.getElementById("createUser").asInstanceOf[js.Dynamic].hidden = true
                document.getElementById("dragonHorde").asInstanceOf[js.Dynamic].hidden = false
                getUserInfo()
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
            document.getElementById("login").asInstanceOf[js.Dynamic].hidden = true
            document.getElementById("dragonHorde").asInstanceOf[js.Dynamic].hidden = false
            document.getElementById("createUser").asInstanceOf[js.Dynamic].hidden = true
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
      document.getElementById("login").asInstanceOf[js.Dynamic].hidden = false
      document.getElementById("dragonHorde").asInstanceOf[js.Dynamic].hidden = true
      document.getElementById("createUser").asInstanceOf[js.Dynamic].hidden = false
    }, e=> {
      println("Fetch error " + e)
    })
  }


  @JSExportTopLevel("getUserInfo")
  def getUserInfo(): Unit = {
    println("loading user info scalajs")
    getStealingInfo()

    getAllHordesInfo()
    getHordeUpgrades()

    FetchJson.fetchGet(getUserInfoRoute, (gold: Int, universalUpgrade: List[String]) => {
      document.getElementById("gold").innerHTML = gold.toString
      for (upgrades <- universalUpgrade) {
        val li = document.createElement("li")
        val text = document.createTextNode()
      }

    }) 
  }

  def getAllHordesInfo(): Unit = {
    println("loading hoards info scalajs.")
    val ul = document.getElementById("horde-section")

    //this currently just has hordes as a list of strings. I think this is okay for now, 
    //but we should get together to see how exactly we want this

    FetchJson.fetchGet(getAllHordesRoute, (hordes: List[String] ) => {
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

def getHordeUpgrades(): Unit = {
  println("getting the horde upgrades")
  // however we want to represent them

  FetchJson.fetchGet(getHordeUpgradesRoute, (upgrades: List[String]) => {
    println("got it. How do we want to display it")
  })
}

def getHordeInfo(): Unit = {
  println("loading one hoards info scalajs")
  FetchJson.fetchGet(getHordeInfoRoute, (horde: HordeInfo) => {
    itemStored = horde.items
    itemIncrement = horde.productionSpeed
    goldConv = horde.goldConversion
  })
}
//def get horde info: return the information of just one hoard in a tuple in horde database model.

  def getStealingInfo(): Unit = {
    println("loading stealing info scalajs.")
    // Example from tasks 

    //this would currently just display the usernames of the vitcims (all users)
    val ul = document.getElementById("stealing-section")
        ul.innerHTML =""
        FetchJson.fetchGet(getStealingInfoRoute, (victims:List[String]) => {
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

  //gets the user's gold amount
  def getGold(): Unit = {
    println("loading gold scalajs.")
    val txt = document.getElementById("goldAmount")
    txt.innerHTML =""
    FetchJson.fetchGet(getGoldRoute, (gold:Double) => {
      goldTotal = gold
      txt.innerHTML =  gold.toString()
        }, e => {
            println("Fetch error: " + e)
        })
  }

  //how much gold they have, what hoards they have -- info with all hoards, universal upgrades.


  //loads info to database when a user clicks on somebody to steal from
  @JSExportTopLevel("stealFromUser")
  def loadSteal(): Unit = {
      println("stealing from user scalajs")
    
        val username = document.getElementById("user").asInstanceOf[html.Input].value
        val victim = document.getElementById("victim").asInstanceOf[html.Input].value    
        val data = models.StealData(username, victim)
        FetchJson.fetchPost(loadStealRoute, csrfToken, data, (bool: Boolean) => {
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


  //should be sure to also save the gold amount into the database in the controller
  //should calculate the amount of gold.
  @JSExportTopLevel("addGold")
  def addGold(): Unit = {
    println("loading gold scalajs")
    val username = document.getElementById("user").asInstanceOf[html.Input].value
    getHordeInfo()
    var gold = goldTotal
    //amount of gold we should have
    gold += (itemStored * goldConv)
    itemStored = 0
    val data = models.GoldData(username,gold)
    FetchJson.fetchPost(addGoldRoute, data, (bool: Boolean) => {
      if (bool) {
        getUserInfo()
      }
      else {
        println("adding gold failed")
      }
    }, e => {
      prtinlnt("Fetch error: " + e)
    })
  }

  //updates interface when user clicks on adding to a horde
  @JSExportTopLevel("addToHorde")
  def addToHorde(): Unit = {
      println("adding to hoard scalajs...")
      val username = document.getElementById("user").asInstanceOf[html.Input].value
      val horde = document.getElementById("horde").asInstanceOf[html.Input].value    
      itemsStored += 1
      document.getElementById("hordeItems").innerHTML = itemStored.toString
      
  }


  //updates database at increments
  def loadHorde(): Unit = {
      println("adding to hoard scalajs...")
      val username = document.getElementById("user").asInstanceOf[html.Input].value
      val horde = document.getElementById("horde").asInstanceOf[html.Input].value    
      val data = models.HordeData(username, horde, itemStored)

      document.getElementById("hordeItems").innerHTML = itemStored.toString
      //if (timer == its time to update database)
      FetchJson.fetchPost(loadHordeRoute, csrfToken, data, (bool: Boolean) => {
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


  //tells the databasae that the user wants to perform
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

  //tells the database that the user wants to perform a universal upgrade
  @JSExportTopLevel("upgradeEverything")
  def upgradeUniversal(): Unit = {
      println("upgrading everything scalajs...")
      val username = document.getElementById("user").asInstanceOf[html.Input].value
      val data = models.User(username)
      FetchJson.fetchPost(upgradeUniversalRoute, csrfToken, data, (bool: Boolean) => {
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

  //tells the database that the user wants to reset their database
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
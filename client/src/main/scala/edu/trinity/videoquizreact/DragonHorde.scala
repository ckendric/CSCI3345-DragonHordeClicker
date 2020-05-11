package edu.trinity.videoquizreact

import org.scalajs.dom
import org.scalajs.dom.MouseEvent
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
import scalajs.js.timers
import scala.concurrent.duration._

object DragonHorde {

    implicit val ex = ExecutionContext.global

    val csrfToken = document.getElementById("csrfToken").asInstanceOf[html.Input].value
    val createRoute = document.getElementById("createRoute").asInstanceOf[html.Input].value
    val createUserHoardsRoute = document.getElementById("createUserHoardsRoute").asInstanceOf[html.Input].value
    val createUserHoardUpgradesRoute = document.getElementById("createUserHoardUpgradesRoute").asInstanceOf[html.Input].value
    val createUniversalUpgradesRoute = document.getElementById("createUniversalUpgradesRoute").asInstanceOf[html.Input].value

    val validateRoute = document.getElementById("validateRoute").asInstanceOf[html.Input].value
    val logoutRoute = document.getElementById("logoutRoute").asInstanceOf[html.Input].value


    val loadHordeRoute = document.getElementById("loadHordeRoute").asInstanceOf[html.Input].value
    val addNewHordeRoute = document.getElementById("addNewHordeRoute").asInstanceOf[html.Input].value
    val addGoldRoute = document.getElementById("addGoldRoute").asInstanceOf[html.Input].value
    
    val upgradeHordeRoute = document.getElementById("upgradeHordeRoute").asInstanceOf[html.Input].value
    val upgradeUniversalRoute = document.getElementById("upgradeUniversalRoute").asInstanceOf[html.Input].value
    val levelUpHordeRoute = document.getElementById("levelUpHordeRoute").asInstanceOf[html.Input].value
    
    val getAllHordesRoute = document.getElementById("getAllHordesRoute").asInstanceOf[html.Input].value
    val getHordeInfoRoute = document.getElementById("getHordeInfoRoute").asInstanceOf[html.Input].value
    val getStealingInfoRoute = document.getElementById("getStealingInfoRoute").asInstanceOf[html.Input].value
    val getGoldRoute = document.getElementById("getGoldRoute").asInstanceOf[html.Input].value
    val getHordeUpgradesRoute = document.getElementById("getHordeUpgradesRoute").asInstanceOf[html.Input].value
    val getUserInfoRoute = document.getElementById("getUserInfoRoute").asInstanceOf[html.Input].value

    val stealFromUserRoute = document.getElementById("stealFromUserRoute").asInstanceOf[html.Input].value
    val resetRoute = document.getElementById("resetRoute").asInstanceOf[html.Input].value




    var itemStored = 0.0
    var itemIncrement = 0.0
    var goldConv = 0.0
    var goldTotal = 0
    var hordeLevel = 0
    var cost = 0
    var id = 0
    var upgradeId = 0
    var upgradeBool = true
    var lastHorde = ""
    var currentHorde = ""
    var username = ""
    var victim = ""
    private val names = List[String]("Rocks and Minerals", "Junk Food", "90s Paraphernalia", "Yarn", "Stuffed Animals", "Cats", "Music Boxes", "Coding Textbooks")
    private val idNames = List[String]("Rocks-and-Minerals", "Junk-Food", "90s-Paraphernalia", "Yarn", "Stuffed-Animals", "Cats", "Music-Boxes", "Coding-Textbooks")

  


    def init(): Unit = {
        println("initializing scala.js")
        document.getElementById("login").asInstanceOf[js.Dynamic].hidden = false
        document.getElementById("createUser").asInstanceOf[js.Dynamic].hidden = true
        document.getElementById("dragonHordeContainer").asInstanceOf[js.Dynamic].hidden = true
    }

    @JSExportTopLevel("showLogin")
    def showLogin(): Unit = {
        println("showing login div scala.js")
        document.getElementById("login").asInstanceOf[js.Dynamic].hidden = false
        document.getElementById("createUser").asInstanceOf[js.Dynamic].hidden = true
        document.getElementById("dragonHordeContainer").asInstanceOf[js.Dynamic].hidden = true
    }

    @JSExportTopLevel("showCreate")
    def showCreate(): Unit = {
        println("showing create user div scala.js")
        document.getElementById("login").asInstanceOf[js.Dynamic].hidden = true
        document.getElementById("createUser").asInstanceOf[js.Dynamic].hidden = false
        document.getElementById("dragonHordeContainer").asInstanceOf[js.Dynamic].hidden = true
    }

    @JSExportTopLevel("login")
    def login(): Unit = {
        println("logging in scalajs")
        username = document.getElementById("loginName").asInstanceOf[html.Input].value
        document.getElementById("username").asInstanceOf[html.Input].innerHTML = username
        val password = document.getElementById("loginPass").asInstanceOf[html.Input].value
        val data = models.UserData(username,password)

        FetchJson.fetchPost(validateRoute,csrfToken, data, (bool:Boolean) => {
             if (bool) {
                getUserInfo()            
                document.getElementById("login").asInstanceOf[js.Dynamic].hidden = true
                document.getElementById("createUser").asInstanceOf[js.Dynamic].hidden = true
                document.getElementById("dragonHordeContainer").asInstanceOf[js.Dynamic].hidden = false
                }
                else {
                    document.getElementById("login-message").innerHTML = "Login Failed"
                }
        }, e => {
            println("Fetch error 1: " + e)
        })
    }

    @JSExportTopLevel("createUser")
    def createUser(): Unit = {
      println("creating user scalajs")
        username = document.getElementById("createName").asInstanceOf[html.Input].value
        val password = document.getElementById("createPass").asInstanceOf[html.Input].value
        val data = models.UserData(username, password)
        FetchJson.fetchPost(createRoute, csrfToken, data, (bool: Boolean) => {
        if(bool) {
            createUserHorde()
        } else {
            document.getElementById("create-message").innerHTML = "User Creation Failed"
        }
    }, e => {
      println("Fetch error 2: " + e)
    })
    
  }

  def createUserHorde() {
    println("creating user horde")
        FetchJson.fetchGet(createUserHoardsRoute, (bool: Boolean) => {
        if(bool) {
            println("created user hordes")
            createUserHordeUpgrades()
        } else {
            document.getElementById("create-message").innerHTML = "User Creation Failed"
        }
    }, e => {
      println("Fetch error 2: " + e)
    })
    
  }

  def createUserHordeUpgrades() {
    println("creating user horde upgrades")
        FetchJson.fetchGet(createUserHoardUpgradesRoute, (bool: Boolean) => {
        if(bool) {
            println("created user hordes")
            createUniversalUpgrades()
        } else {
            document.getElementById("create-message").innerHTML = "User Creation Failed"
        }
    }, e => {
      println("Fetch error 2: " + e)
    })
    
  }

  def createUniversalUpgrades() {
    println("creating universal upgrades scalajs")
    FetchJson.fetchGet(createUniversalUpgradesRoute, (bool: Boolean) => {
    if(bool) {
        println("created user hordes")
        logout()
    } else {
        document.getElementById("create-message").innerHTML = "User Creation Failed"
    }
    }, e => {
      println("Fetch error 2: " + e)
    })
  }



  @JSExportTopLevel("logout")
  def logout():Unit = {
  println("logging out")
    FetchJson.fetchGet(logoutRoute, (bool:Boolean) => {
      document.getElementById("login").asInstanceOf[js.Dynamic].hidden = false
      document.getElementById("dragonHordeContainer").asInstanceOf[js.Dynamic].hidden = true
      document.getElementById("createUser").asInstanceOf[js.Dynamic].hidden = true
      itemStored = 0.0
      itemIncrement = 0.0
      goldConv = 0.0
      goldTotal = 0
      hordeLevel = 0
      cost = 0
      id = 0
      upgradeId = 0
      upgradeBool = true
      lastHorde = ""
      currentHorde = ""
    }, e=> {
      println("Fetch error 3:" + e)
    })

    
  }


  @JSExportTopLevel("getUserInfo")
  def getUserInfo(): Unit = {
    println("loading user info scalajs")
    getStealingInfo()
    getAllHordesInfo()
    //getHordeUpgrades()

    /*FetchJson.fetchGet(getUserInfoRoute, (gold: (Int, List[String])) => {
      document.getElementById("gold").innerHTML = gold._1.toString
      for (upgrade <- gold._2) {
        val li = document.createElement("li")
        val text = document.createTextNode(upgrade)
      }
    }, e => {
      println("Fetch error 4: " + e)
    })*/
    getGold()
  }

  def setCurrentHorde(name:String) {
    currentHorde = name
    loadOneHorde()
  }

  def getAllHordesInfo(): Unit = {
    println("loading hoards info scalajs.")
    val ul = document.getElementById("horde-section")
    FetchJson.fetchGet(getAllHordesRoute, (hordes: Seq[Boolean] ) => {
      for(i <- 0 until hordes.length) {
        if(hordes(i)) {
          val li = document.createElement("li")
          li.id = idNames(i)
          li.addEventListener("click", { (e0: dom.Event) =>
            val e = e0.asInstanceOf[dom.MouseEvent]
            println(idNames(i))
            setCurrentHorde(names(i))
          }, false)


          println(currentHorde)
          val text = document.createTextNode(names(i))
          li.appendChild(text)
          ul.appendChild(li)
          lastHorde = names(i)
        }
      }
    }, e => {
      println("Fetch error 5: " + e)
    })
}

def getHordeUpgrades(hordeId: Int): Unit = {
  println("getting the horde upgrades")
  // however we want to represent them

  val data = models.HordeId(hordeId)
  FetchJson.fetchPost(getHordeUpgradesRoute, csrfToken, data, (upgrades: List[String]) => {
    println("got it. How do we want to display it")
    }, e => {
      println("Fetch error 6: " + e)
    })

}

def loadOneHorde(): Unit = {
  println("super-beginning")
      val data = models.UserHorde(username, currentHorde)
      println("after data")
      //if (timer == its time to update database)
      FetchJson.fetchPost(getHordeInfoRoute, csrfToken, data, (horde: (Int, Int, Int, Double, Double, Double, Boolean)) => {
        println("beginning")
        itemStored = horde._4
        itemIncrement = horde._5
        goldConv = horde._6
        hordeLevel = horde._3
        cost = horde._2
        id = horde._1
        getHordeUpgrades(id)
        document.getElementById("conversionRate").innerHTML = goldConv.toString
        document.getElementById("hordeItems").innerHTML = itemStored.toString
        document.getElementById("buttons").asInstanceOf[js.Dynamic].hidden = false
        println("ending")
      }, e => {
          println("Fetch error 7: " + e)
    })

    /*js.timers.setInterval(3) {
      itemStored += itemIncrement.toInt
      //document.getElementById("hordeItems").innerHTML = itemStored.toString
    }
    js.timers.setInterval(150) {
      loadHorde()
    }*/
}


def loadHorde(): Unit = {
    println("loading horde info scalajs")
    var gold = goldTotal
    //amount of gold we should have
    gold += (itemStored * goldConv).toInt
    itemStored = 0
    val data = models.LoadHorde(id, cost, hordeLevel, itemStored, itemIncrement, goldConv, true)
    FetchJson.fetchPost(loadHordeRoute, csrfToken,data, (bool: Boolean) => {
      if (bool) {
        getUserInfo()
        loadOneHorde()
      }
      else {
        println("loading horde info failed")
      }
    }, e => {
        println("Fetch error 13: " + e)
    })

}

def getHordeUpgradesInfo(horde: String): Unit = {
      val data = models.UserHorde(username, horde)
      //if (timer == its time to update database)
      FetchJson.fetchPost(loadHordeRoute, csrfToken, data, (upgrades: (Int, Int, Int, Boolean, Double, Double)) => {
          upgradeId = upgrades._1
          upgradeBool = upgrades._4
      }, e => {
          println("Fetch error 8: " + e)
    })
}

def setVictim(name: String) {
    victim = name
    stealFromUser()
}

  def getStealingInfo(): Unit = {
    println("loading stealing info scalajs.")
    // Example from tasks 

    //this would currently just display the usernames of the vitcims (all users)
    val ul = document.getElementById("stealing-section")
        ul.innerHTML =""
        FetchJson.fetchGet(getStealingInfoRoute, (victims:Seq[(Int, String)]) => {
            for(victim <- victims) {
                      val li = document.createElement("li")
                      li.id = victim._1.toString
                      li.addEventListener("click", { (e0: dom.Event) =>
                        val e = e0.asInstanceOf[dom.MouseEvent]
                        println(victim._2)
                        setVictim(victim._2)
                      }, false)
                      val text = document.createTextNode("steal from: " + victim._2)
                      li.appendChild(text)
                      ul.appendChild(li)
            }
        }, e => {
            println("Fetch error 10: " + e)
        })
  }

  //gets the user's gold amount
  def getGold(): Unit = {
    println("loading gold scalajs.")
    val txt = document.getElementById("gold")
    FetchJson.fetchGet(getGoldRoute, (gold:Int) => {
      goldTotal = gold
      txt.innerHTML =  gold.toString()
        }, e => {
            println("Fetch error 11: " + e)
        })
  }

  //how much gold they have, what hoards they have -- info with all hoards, universal upgrades.


  //loads info to database when a user clicks on somebody to steal from
  @JSExportTopLevel("stealFromUser")
  def stealFromUser(): Unit = {
      println("stealing from user scalajs")
        val data = models.User(victim)
        //returns horde name and amount stolen
        FetchJson.fetchPost(stealFromUserRoute, csrfToken, data, (stolen:(String,  Int)) => {
        if(stolen._1 != "") {
            println("successfully stole from " + victim)
            getUserInfo()
        } else {
            document.getElementById("create-message").innerHTML = "Stealing Failed"
        }
    }, e => {
      println("Fetch error 12: " + e)
    })
  }


  //should be sure to also save the gold amount into the database in the controller
  //should calculate the amount of gold.
  @JSExportTopLevel("addGold")
  def addGold(): Unit = {
    println("loading gold scalajs")
    loadOneHorde()
    var gold = goldTotal
    //amount of gold we should have
    gold += (itemStored * goldConv).toInt
    itemStored = 0
    val data = models.GoldData(username,gold)
    FetchJson.fetchPost(addGoldRoute, csrfToken,data, (bool: Boolean) => {
      if (bool) {
        getUserInfo()
        loadOneHorde()
      }
      else {
        println("adding gold failed")
      }
    }, e => {
        println("Fetch error 13: " + e)
    })
  }

  //updates interface when user clicks on adding to a horde
  @JSExportTopLevel("addToHorde")
  def addToHorde(): Unit = {
      println("adding to hoard scalajs...")
      itemStored += 1
      document.getElementById("hordeItems").innerHTML = itemStored.toString
      
  }

  @JSExportTopLevel("unlockNewHorde")
  def unlockNewHorde(): Unit = {
      for (x <- 0 to names.length -1) {
        if (lastHorde == names(x)) {
          lastHorde = names(x + 1)
        }
      }
      if (lastHorde == "") {
        lastHorde = names(0)
      }
      val data = models.AddNewHorde(username,lastHorde,goldTotal)
      FetchJson.fetchPost(addNewHordeRoute, csrfToken, data, (bool: Boolean) => {
        if (bool) {
          println("successfully leveled up horde")
          getUserInfo()
        }
        else {
          println("leveling up failed")
        }
      }, e => {
        println("Fetch error 15: " + e)
      })
  }

  
  @JSExportTopLevel("levelUpHorde")
  def levelUpHorde(): Unit = {
    println("leveling up hoard scalajs")
    val data = models.LevelUpData(id, hordeLevel, itemIncrement, cost, goldTotal)
    FetchJson.fetchPost(levelUpHordeRoute, csrfToken, data, (bool: Boolean) => {
      if (bool) {
        println("successfully leveled up horde")
        getUserInfo()
      }
      else {
        println("leveling up failed")
      }
    }, e => {
        println("Fetch error 16: " + e)
    })
  }

  //    * 1. hoardId
  //    * 2. hoard productionSpeed
  //    * 3. hoard goldConversionRate
   //   * 4. upgradeId
   //   * 5. upgrade's unlocked boolean value (though I could probably just assume this as True) 

  //tells the databasae that the user wants to perform
  @JSExportTopLevel("upgradeHorde")
  def upgradeHorde(): Unit = {
      println("upgrading hoard scalajs...")
      val horde = document.getElementById("hode").asInstanceOf[html.Input].value    
      val data = models.UpgradeHorde(id, itemIncrement, goldConv, upgradeId, upgradeBool)
      FetchJson.fetchPost(upgradeHordeRoute, csrfToken, data, (bool: Boolean) => {
         if(bool) {
            println("successfully upgraded " + horde)
            getUserInfo()
        } else {
            document.getElementById("create-message").innerHTML = "Upgrading Horde Failed"
      }
    }, e => {
      println("Fetch error 17: " + e)
    })   
  }

  //tells the database that the user wants to perform a universal upgrade
  @JSExportTopLevel("upgradeEverything")
  def upgradeUniversal(): Unit = {
      println("upgrading everything scalajs...")
      val data = models.User(username)
      FetchJson.fetchPost(upgradeUniversalRoute, csrfToken, data, (bool: Boolean) => {
         if(bool) {
            println("successfully upgradded eveything")
            getUserInfo()
        } else {
            document.getElementById("create-message").innerHTML = "User Creation Failed"
      }
    }, e => {
      println("Fetch error 18: " + e)
    })   
  }

  //tells the database that the user wants to reset their database
  @JSExportTopLevel("reset")
  def reset(): Unit = {
      val data = models.User(username)
      FetchJson.fetchPost(resetRoute, csrfToken, data, (bool: Boolean) => {
         if(bool) {
            println("successfully reset eveything")
            getUserInfo()
        } else {
            document.getElementById("create-message").innerHTML = "Resetting Failed"
      }
    }, e => {
      println("Fetch error 19: " + e)
    })   
  }

}
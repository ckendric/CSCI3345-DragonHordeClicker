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
import scalajs.js.timers

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
    private val names = List[String]("Rocks and Minerals", "Junk Food", "90s Paraphernalia", "Yarn", "Stuffed Animals", "Cats", "Music Boxes", "Coding Textbooks")

  


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
        val username = document.getElementById("loginName").asInstanceOf[html.Input].value
        val password = document.getElementById("loginPass").asInstanceOf[html.Input].value
        val data = models.UserData(username,password)

        FetchJson.fetchPost(validateRoute,csrfToken, data, (bool:Boolean) => {
             if (bool) {
                getUserInfo()            
                document.getElementById("login").asInstanceOf[js.Dynamic].hidden = true
                document.getElementById("createUser").asInstanceOf[js.Dynamic].hidden = true
                document.getElementById("dragonHordeContainer").asInstanceOf[js.Dynamic].hidden = false
                document.getElementById("username").innerHTML = username
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
        val username = document.getElementById("createName").asInstanceOf[html.Input].value
        val password = document.getElementById("createPass").asInstanceOf[html.Input].value
        val data = models.UserData(username, password)
        FetchJson.fetchPost(createRoute, csrfToken, data, (bool: Boolean) => {
        if(bool) {
            getUserInfo()
            document.getElementById("login").asInstanceOf[js.Dynamic].hidden = true
            document.getElementById("dragonHordeContainer").asInstanceOf[js.Dynamic].hidden = false
            document.getElementById("createUser").asInstanceOf[js.Dynamic].hidden = true
            document.getElementById("username").innerHTML = username
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

  def getAllHordesInfo(): Unit = {
    println("loading hoards info scalajs.")
    val ul = document.getElementById("horde-section")
    FetchJson.fetchGet(getAllHordesRoute, (hordes: List[String] ) => {
      for(horde <- hordes) {
        val li = document.createElement("li")
        li.id = horde
        val text = document.createTextNode(horde)
        li.appendChild(text)
        ul.appendChild(li)
        lastHorde = horde
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

def loadOneHorde(horde: String): Unit = {
      val username = document.getElementById("username").asInstanceOf[html.Input].value
      val data = models.UserHorde(username, horde)
      currentHorde = horde
      document.getElementById("hordeItems").innerHTML = itemStored.toString
      //if (timer == its time to update database)
      FetchJson.fetchPost(loadHordeRoute, csrfToken, data, (horde: (Int, Int, Int, Double, Double, Double)) => {
        itemStored = horde._4
        itemIncrement = horde._5
        goldConv = horde._6
        hordeLevel = horde._3
        cost = horde._2
        id = horde._1
        getHordeUpgrades(id)
      }, e => {
          println("Fetch error 7: " + e)
    })

    js.timers.setInterval(3) {
      itemStored += itemIncrement.toInt
      document.getElementById("hordeItems").innerHTML = itemStored.toString
    }
    js.timers.setInterval(150) {
      loadHorde()
    }
}

def getHordeUpgradesInfo(horde: String): Unit = {
      val username = document.getElementById("username").asInstanceOf[html.Input].value
      val data = models.UserHorde(username, horde)
      document.getElementById("hordeItems").innerHTML = itemStored.toString
      //if (timer == its time to update database)
      FetchJson.fetchPost(loadHordeRoute, csrfToken, data, (upgrades: (Int, Int, Int, Boolean, Double, Double)) => {
          upgradeId = upgrades._1
          upgradeBool = upgrades._4
      }, e => {
          println("Fetch error 8: " + e)
    })
}


//(id: Int, cost:Int, level:Int, items: Double, productionSpeed: Double, goldConversion: Double)
//this may need to turn into a post bc I will need to give specific horde's id...
def getHordeInfo(): Unit = {
  println("loading one hoards info scalajs")
  FetchJson.fetchGet(getHordeInfoRoute, (horde: (Int, Int, Int, Double, Double, Double)) => {
    itemStored = horde._4
    itemIncrement = horde._5
    goldConv = horde._6
    hordeLevel = horde._3
    cost = horde._2
    id = horde._1
  }, e => {
      println("Fetch error 9: " + e)
    })

    js.timers.setInterval(3) {
      itemStored += itemIncrement.toInt
      document.getElementById("hordeItems").innerHTML = itemStored.toString
    }

    js.timers.setInterval(150) {
      loadHorde()
    }
}
//def get horde info: return the information of just one hoard in a tuple in horde database model.

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
    txt.innerHTML =""
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
        val victim = document.getElementById("victim").asInstanceOf[html.Input].value    
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
    val username = document.getElementById("username").asInstanceOf[html.Input].value
    loadOneHorde(currentHorde)
    var gold = goldTotal
    //amount of gold we should have
    gold += (itemStored * goldConv).toInt
    itemStored = 0
    val data = models.GoldData(username,gold)
    FetchJson.fetchPost(addGoldRoute, csrfToken,data, (bool: Boolean) => {
      if (bool) {
        getUserInfo()
        loadOneHorde(currentHorde)
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
      val username = document.getElementById("username").asInstanceOf[html.Input].value
      val horde = document.getElementById("horde").asInstanceOf[html.Input].value    
      itemStored += 1
      document.getElementById("hordeItems").innerHTML = itemStored.toString
      
  }


  //updates database at increments
  def loadHorde(): Unit = {
      println("adding to hoard scalajs...")
      val username = document.getElementById("username").asInstanceOf[html.Input].value
      val horde = document.getElementById("horde").asInstanceOf[html.Input].value    
      val data = models.HordeData(username, horde, itemStored)

      document.getElementById("hordeItems").innerHTML = itemStored.toString
      //if (timer == its time to update database)
      FetchJson.fetchPost(loadHordeRoute, csrfToken, data, (bool: Boolean) => {
         if(bool) {
            println("successfully added to " + horde)
            getUserInfo()
        } else {
            document.getElementById("create-message").innerHTML = "Adding to horde Failed"
      }
    }, e => {
      println("Fetch error 14: " + e)
    })    
  }
      // What I get passed:
      // username
      // next horde to upgrade
      // user's gold

  @JSExportTopLevel("unlockNewHorde")
  def unlockNewHorde(): Unit = {
      val username = document.getElementById("username").asInstanceOf[html.Input].value
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
      val username = document.getElementById("username").asInstanceOf[html.Input].value
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
      val username = document.getElementById("username").asInstanceOf[html.Input].value
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
      val username = document.getElementById("username").asInstanceOf[html.Input].value
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
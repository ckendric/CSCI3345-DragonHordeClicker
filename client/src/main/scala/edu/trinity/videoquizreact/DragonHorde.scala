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
import slinky.web.svg.d

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
    var victimid = -1
    private val names = List[String]("Rocks and Minerals", "Junk Food", "90s Paraphernalia", "Yarn", "Stuffed Animals", "Cats", "Music Boxes", "Coding Textbooks")
    private val idNames = List[String]("Rocks-and-Minerals", "Junk-Food", "90s-Paraphernalia", "Yarn", "Stuffed-Animals", "Cats", "Music-Boxes", "Coding-Textbooks")
    private val mapRoutes = Map[Int,String](1->"rocksandminerals.jpg",
                                            2->"junkfood.jpg",
                                            3->"ninetiesparaphernalia.jpg",
                                            4->"yarn.jpg",
                                            5->"stuffedanimals.jpg",
                                            6->"cats.jpg",
                                            7->"musicboxes.jpg",
                                            8->"codingtextbooks.jpg",
                                            9->"marklewis.jpg")
  


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
        FetchJson.fetchGet(createUserHoardUpgradesRoute, (bool: Boolean) => {
        if(bool) {
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
    getStealingInfo()
    getAllHordesInfo()
    //getHordeUpgrades()
    getGold()
  }

  def setCurrentHorde(name:String) {
    currentHorde = name
    loadOneHorde()
  }

  def getAllHordesInfo(): Unit = {
    val ul = document.getElementById("horde-section")
    FetchJson.fetchGet(getAllHordesRoute, (hordes: Seq[Boolean] ) => {
      if(currentHorde != "") loadHorde()
      var noUnlocked = 0;
      for(i <- 0 until hordes.length){ if(hordes(i)) noUnlocked+=1 }
      for(i <- 0 until noUnlocked) {
        println(hordes(i))
        val li = document.createElement("li")
        li.id = idNames(i)
        println(names(i))
        li.addEventListener("click", { (e0: dom.Event) =>
          val e = e0.asInstanceOf[dom.MouseEvent]
          loadHorde()
          setCurrentHorde(names(i))
        }, false)
        val text = document.createTextNode(names(i))
        li.appendChild(text)
        ul.appendChild(li)
        lastHorde = names(i)
      }
    }, e => {
      println("Fetch error 5: " + e)
    })
}

def getNewHordeInfo(): Unit = {
  val ul = document.getElementById("horde-section")
  FetchJson.fetchGet(getAllHordesRoute, (hordes: Seq[Boolean] ) => {
    var noUnlocked = -1;
    for(i <- 0 until hordes.length){ if(hordes(i)) noUnlocked+=1 }
    val li = document.createElement("li")
    li.id = idNames(noUnlocked)
    println(names(noUnlocked))
    li.addEventListener("click", { (e0: dom.Event) =>
      val e = e0.asInstanceOf[dom.MouseEvent]
      setCurrentHorde(names(noUnlocked))
    }, false)
    val text = document.createTextNode(names(noUnlocked))
    li.appendChild(text)
    ul.appendChild(li)
    lastHorde = names(noUnlocked)
  }, e => {
      println("Fetch error 5: " + e)
  })
}

def getHordeUpgrades(hordeId: Int): Unit = {
  // however we want to represent them

  val data = hordeId
  FetchJson.fetchPost(getHordeUpgradesRoute, csrfToken, data, (upgrades: List[String]) => {
    println("got it. How do we want to display it")
    }, e => {
      println("Fetch error 6: " + e)
    })

}

def loadOneHorde(): Unit = {  
      val hordeNumber = names.indexOf(currentHorde)+1
      val data = hordeNumber
      FetchJson.fetchPost(getHordeInfoRoute, csrfToken, data, (horde: (Int, Int, Int, Double, Double, Double, Boolean)) => {
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
        document.getElementById("dragonimage").asInstanceOf[html.Image].src = "versionedAssets/images/"+mapRoutes(id)
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
    val data = models.LoadHorde(id, cost, hordeLevel, itemStored, itemIncrement, goldConv, true)
    FetchJson.fetchPost(loadHordeRoute, csrfToken,data, (bool: Boolean) => {
      if (bool) {
        loadOneHorde()
        getGold()
      }
      else {
        println("loading horde info failed")
      }
    }, e => {
        println("Fetch error 13: " + e)
    })

}

def getHordeUpgradesInfo(horde: String): Unit = {
      val data = models.HordeId(names.indexOf(horde)+1)
      FetchJson.fetchPost(loadHordeRoute, csrfToken, data, (upgrades: (Int, Int, Int, Boolean, Double, Double)) => {
          upgradeId = upgrades._1
          upgradeBool = upgrades._4
      }, e => {
          println("Fetch error 8: " + e)
    })
}

def setVictim(name: String, id:Int) {
    victim = name
    victimid = id
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
                        setVictim(victim._2,victim._1)
                      }, false)
                      val text = document.createTextNode("steal from: " + victim._2)
                      li.appendChild(text)
                      ul.appendChild(li)
            }
        }, e => {
            println("Fetch error 10: " + e)
        })
  }

  //gets the user's gold amount from database and displays it
  def getGold(): Unit = {
    println("loading gold scalajs.")
    val txt = document.getElementById("gold")
    FetchJson.fetchGet(getGoldRoute, (gold:Int) => {
      goldTotal = gold
      txt.innerHTML =  goldTotal.toString()
        }, e => {
            println("Fetch error 11: " + e)
        })
  }

  //how much gold they have, what hoards they have -- info with all hoards, universal upgrades.


  //loads info to database when a user clicks on somebody to steal from
  @JSExportTopLevel("stealFromUser")
  def stealFromUser(): Unit = {
      println("stealing from user scalajs")
        val data = victimid
        //returns horde name and amount stolen
        FetchJson.fetchPost(stealFromUserRoute, csrfToken, data, (stolen:(String,  Double)) => {
        if(stolen._1 != "") {
            val msg = "Successfully stole "+stolen._2+" items from " + victim + "\'s "+stolen._1+" hoard."
            println(msg)
            loadOneHorde()
        } else {
            println("did a bad")
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
    println("adding gold scalajs")
    //amount of gold we should have
    goldTotal += (itemStored * goldConv).toInt
    itemStored = 0
    val data = goldTotal
    FetchJson.fetchPost(addGoldRoute, csrfToken,data, (bool: Boolean) => {
      if (bool) {
        document.getElementById("gold").innerHTML = goldTotal.toString()
        loadHorde()
        getGold()
        loadOneHorde()
        if (currentHorde == lastHorde && goldTotal >= cost){
          document.getElementById("unlockNewHoardButton").asInstanceOf[js.Dynamic].disabled = false
        }
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
      if(itemStored < 1000000) itemStored += 1
      document.getElementById("hordeItems").innerHTML = itemStored.toString
      
  }

  @JSExportTopLevel("unlockNewHorde")
  def unlockNewHorde(): Unit = {
      println(lastHorde)
      if (lastHorde == "") {
        lastHorde = names(0)
      }
      goldTotal -= cost
      val hoardNumber = names.indexOf(lastHorde)+2
      println(hoardNumber)
      val data = (hoardNumber, true, goldTotal)
      println("calling unlock new hoard")
      FetchJson.fetchPost(addNewHordeRoute, csrfToken, data, (bool: Boolean) => {
        if (bool) {
          document.getElementById("unlockNewHoardButton").asInstanceOf[js.Dynamic].disabled = true
          println("successfully unlocked new horde")
          getNewHordeInfo()
          getGold()
        }
        else {
          println("unlocking horde failed")
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
      val horde = document.getElementById("horde").asInstanceOf[html.Input].value    
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
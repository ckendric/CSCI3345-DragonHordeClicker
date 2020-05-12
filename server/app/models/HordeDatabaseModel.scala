package model

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import models.Tables._
import scala.concurrent.Future
import org.mindrot.jbcrypt.BCrypt //import mindrot in build.sbt
import com.typesafe.sslconfig.ssl.FakeChainedKeyStore.User
import collection.mutable

class HordeDatabaseModel(db: Database)(implicit ec: ExecutionContext) {
    private val cost = List[Int](100, 1000, 10000, 250000, 1000000, 10000000, 250000000, 1000000000, 0)
    //level upgrade cost multiplier: all 1.35 except Dr. Lewis which is 1
    //collection rate multiplier: all 1.1 except Dr. Lewis which is 1
    private val conversionRate = List[Double](1.0, 1.0/10.0, 1.0, 20.0, 75.0, 700.0, 15000.0, 40000.0, 1)
    //cap: all 100K except Lewis which is 1
    private val names = List[String]("Rocks and Minerals", "Junk Food", "90s Paraphernalia", "Yarn", "Stuffed Animals", "Cats", "Music Boxes", "Coding Textbooks")

    //hoard-specific upgrades
//these descriptions might not even need to be in here
        
    private val hoardUpgradeCosts = List[Int](25, 300, 4000, 7000, 20000, 100000)
        //might be a double
    private val hoardUpgradeNewSpeeds = List[Double](1, 4, 4, 7, 12, 20)
    private val hoardUpgradeGoldMultipliers = List[Double](1, 1, 2, 1, 1, 1)


    //universal upgrades

    //WARNING - Gold may have to be a double
    //... doubles, it's all doubles


    def validateUser(username:String,password:String):Future[Option[Int]] = {
        val matches = db.run(Users.filter(userRow => userRow.username === username).result)
        matches.map(userRows => userRows.headOption.flatMap{
            userRow => if (BCrypt.checkpw(password,userRow.password)) Some(userRow.id) else None
        })
    }

    def createUser(username:String,password:String):Future[Option[Int]] = {
        val matches = db.run(Users.filter(userRow => userRow.username === username).result)
        //initialises user if it does not already exist
        matches.flatMap{ userRows => 
            if(userRows.nonEmpty) {
                Future.successful(None)
            } else {
                db.run(Users += UsersRow(-1, username, BCrypt.hashpw(password, BCrypt.gensalt()), 0))
                .flatMap{ addCount => 
                    if (addCount > 0) db.run(Users.filter(userRow => userRow.username === username).result)
                        .map(_.headOption.map(_.id))
                    else Future.successful(None)
                }
            }
        }
        //Future.successful(true)
    }
    def createUserHoards(userId:Int):Future[Boolean] = {
        //initialises all hoards for the user
        var i = 0
        var unlocked = true
        for(i <- 1 to 9) {
            if(i > 1) unlocked = false
                db.run(Hoard += HoardRow(-1, userId, 
                        /*hoardType*/i, 
                        /*cost*/cost(i-1), 
                        /*HoardLevel*/0, 
                        /*HoardItems*/0, 
                        /*ProductionSpeed*/0, 
                        /*Gold Conversion*/conversionRate(i-1),
                        /*unlocked*/unlocked))
        }
        Future.successful(true)
    }
    def createUserHoardUpgrades(userId:Int):Future[Boolean] = {
        //createUserHoardUpgrades
        //initialises all hoard-specific upgrades
        var j = 0;
        var i = 0;
        val hoards = db.run(Hoard.filter(hoardRow => hoardRow.userId === userId).result)
        for(i <- 0 to 8){
            for(j <- 1 to 6){
                hoards.flatMap { hoards =>
                    db.run(Hoardupgrade += HoardupgradeRow(-1, 
                                                       /*hoardId*/hoards(i).hoardId, 
                                                       /*upgradeNo*/j, 
                                                       /*cost*/hoardUpgradeCosts(j-1),
                                                       /*unlocked*/false,
                                                       /*newspeed*/hoardUpgradeNewSpeeds(j-1),
                                                       /*goldmultiplier*/hoardUpgradeGoldMultipliers(j-1)))
                }
            }
        }
        Future.successful(true)
    }
    def createUniversalUpgrades(userId:Int):Future[Boolean] = {
        //createUniversalUpgrades
        //initialises all universal upgrades
        for(i <- 0 to 4){
                db.run(Univupgrades += UnivupgradesRow(-1,userId,i,false))
        }
        Future.successful(true)
    }

    def getUserInfo(userid:Int):Future[(Option[Int], Seq[Boolean])] = {
        Future.successful((None, Seq[Boolean]()))
    }


    //need to pass info about which hoard is which because it gets put out of order in the db
    def getAllHordesInfo(userid:Int):Future[Seq[Boolean]] = {
        //unlocked hoards
        var hoards = db.run((for {hoard <- Hoard if hoard.userId === userid} yield {hoard.unlocked}).result)
        for{ h <- hoards } yield { h }
    }
    
    //returns: Future[(Int, Int, Int, Double, Double, Double, Boolean)]
    //    get all information for one of the user's hoards when said hoard is selected
    def getHoardInfo(userid:Int, hoardType:Int):Future[(Int, Int, Int, Double, Double, Double, Boolean)] = {
        //reult of all hoards with userid and hoardType
        val matches = db.run(Hoard.filter(hoard => hoard.userId === userid && hoard.hoardtype === hoardType).result)
        matches.flatMap{ hoardRows => 
            //returns all relevant hoard data if hoard is unlocked (designated by final bool)
            if(hoardRows.head.unlocked) {
                Future.successful((hoardRows.head.hoardtype, hoardRows.head.cost, hoardRows.head.hoardlevel, 
                                   hoardRows.head.hoarditems, hoardRows.head.productionspeed, hoardRows.head.goldconversionrate,
                                   true))
            } 
            //returns all 0 and unlocked=false if not unlocked
            //shouldn't ever happen I think
            else {
                Future.successful((0,0,0,0.0,0.0,0.0,false))
            }
        }
    }

    def getHoardUpgradesInfo(userid:Int, hoardType:Int):Future[Seq[(Int, Int, Int, Boolean, Double, Double)]] = {
        //val matches = db.run(Hoard.filter(hoard => hoard.userId === userid && hoard.hoardtype == hoardType).result)
        var i = 0;
        //first get proper hoard ID
        val hoardID = db.run((for {hoard <- Hoard if hoard.userId === userid && hoard.hoardtype === hoardType} yield {hoard.hoardId}).result)
        val matches = hoardID.flatMap { ids => db.run(Hoardupgrade.filter(upgrade => upgrade.hoardId === ids.head).result)}
        
        Future.sequence(for(i <- 0 to 5) 
        yield {matches.flatMap{ upgradeRows => Future.successful((upgradeRows(i).hoardupgradeId, 
                                                                  upgradeRows(i).upgradeno, 
                                                                  upgradeRows(i).cost, 
                                                                  upgradeRows(i).unlocked, 
                                                                  upgradeRows(i).newspeed, 
                                                                  upgradeRows(i).goldmultiplier))}})
    }

    def getOneHoardUpgradeInfo(userid:Int, upgradeId:Int):Future[(Int, Int, Int, Boolean, Double, Double)] = {
        db.run(Hoardupgrade.filter(u => u.hoardupgradeId === upgradeId).result).flatMap{ u =>
            Future.successful((u.head.hoardupgradeId, u.head.upgradeno, u.head.cost, u.head.unlocked, u.head.newspeed, u.head.goldmultiplier))
        }
    }

    //returns: Future[(Seq[Int],Seq[String])]
    //         a list of other userids and usernames, supposedly to be stolen from
    //I think this may need to be changed later on, but the rest might be handled in stealFromUser
    def getStealingInfo(userId:Int):Future[Seq[(Int, String)]] = {
        db.run((for {user <- Users if !(user.id === userId)} yield { (user.id, user.username) }).result)
    }

    //randomly picks a hoard from a user and attempts to steal from them at a given probability
    /** Concerns:
      * 
      * - probability calculation: randomise how many items to be stolen
      * - gaussian random multiplied by how much in the hoard
      * - probably need to know what hoards the stealing user has (maybe I can do that?)
      *     Steps:
      *        1. get user hoards
      *        2. get victim hoards
      *        3. get min length of hoards
      *        4. pick rand of that min length
      *        5. select a random amount of items from victim
      *        6. update victim hoard amount
      *        7. update user hoard amount
      * - reurn how much was stolen from which hoard
      * 
      */
    def stealFromUser(userid:Int, username:String, stolen:Int):Future[(String, Double)] = {
        val r = scala.util.Random
        val nrand = (r.nextDouble()) //value to multiply hoard contents by
        val unlockedHoards = db.run((for {hoard <- Hoard if hoard.userId === userid} yield {(hoard.unlocked,hoard.hoarditems,hoard.hoardtype)}).result)
        val victimHoards = db.run((for {hoard <- Hoard if hoard.userId === stolen} yield {(hoard.unlocked,hoard.hoarditems,hoard.hoardtype)}).result)
        val stealAmount = unlockedHoards.flatMap { userHoards =>
            victimHoards.flatMap { victimHoards =>
                val commonHoards = scala.math.min(userHoards.filter(_._1==true).length,victimHoards.filter(_._1==true).length)
                val stealHoardNum = r.nextInt(commonHoards)+1
                var amountToSteal = 0.0;
                for(h <- victimHoards){
                    if(h._3 == stealHoardNum) amountToSteal = h._2*nrand
                }
                val updateVictim = db.run((for {h <- Hoard if h.userId === stolen && h.hoardtype === stealHoardNum} yield {h.hoarditems}).result)
                updateVictim.flatMap{ victimItems =>
                    db.run((for {h <- Hoard if h.userId === stolen && h.hoardtype === stealHoardNum} yield {h.hoarditems}).update(victimItems.head-amountToSteal))
                }
                //updateVictim.update(updateVictim-amountToSteal)
                val updateUser = db.run((for {h <- Hoard if h.userId === userid && h.hoardtype === stealHoardNum} yield {h.hoarditems}).result)
                updateUser.flatMap{ userItems =>
                    var newItemAmount = 0.0;
                    if(userItems.head+amountToSteal > 1000000) newItemAmount = 1000000
                    else newItemAmount = userItems.head+amountToSteal
                    db.run((for {h <- Hoard if h.userId === userid && h.hoardtype === stealHoardNum} yield {h.hoarditems}).update(newItemAmount))
                }
                //updateUser.update(updateUser+amountToSteal)

                val hoardName = names(stealHoardNum-1)
                Future.successful(hoardName,amountToSteal)
            }
        }
        //Future.successful(("",1))
        stealAmount
    }

    def addGold(userId:Int, username:String, newGold:Int):Future[Int] = {
        db.run((for { u <- Users if u.id === userId} yield {u.gold}).update(newGold))
        Future.successful(1)
    }

    def getGold(userid:Int):Future[Option[Int]] = {
        db.run((for {user <- Users if user.id === userid} yield {user.gold}).result).map(userRows => userRows.headOption)
    }

    def loadHoardInfo(userid:Int, hoardType:Int, newCost:Int, newLv:Int, items:Double, newSpeed:Double, newConversionRate:Double, unlocked:Boolean):Future[Int] = {
        println("In database loadhoardinfo")
        db.run((for {h <- Hoard if h.userId === userid && h.hoardtype === hoardType} yield {h.cost}).update(newCost))
        db.run((for {h <- Hoard if h.userId === userid && h.hoardtype === hoardType} yield {h.hoardlevel}).update(newLv))
        db.run((for {h <- Hoard if h.userId === userid && h.hoardtype === hoardType} yield {h.hoarditems}).update(items))
        db.run((for {h <- Hoard if h.userId === userid && h.hoardtype === hoardType} yield {h.productionspeed}).update(newSpeed))
        db.run((for {h <- Hoard if h.userId === userid && h.hoardtype === hoardType} yield {h.goldconversionrate}).update(newConversionRate))
        db.run((for {h <- Hoard if h.userId === userid && h.hoardtype === hoardType} yield {h.unlocked}).update(unlocked))
        Future.successful(1)
    }

    /** Process:
      * 
      * What I get passed:
      * 
      */
    def upgradeUniversal(userid:Int, username:String, dEFINE:Int):Future[Int] = {
        Future.successful(1)
    }

    /** Process:
      * 1. set new production speed
      *     -this gets a bit complicated as the speed is also affected by what level the hoard is. The process should be as follows:
      *         a. reassign the pruction speed to the production speed specified by the upgrade (i.e. the second speed upgrade is 4)
      *         b. next recalculate the multipliers based on the hoard's level. So take the reassigned speed (i.e. 4) and 
      *            multiply it by the level of the hoard multiplier * 1.1
      *            HOWEVER: if the hoard level is currently 0 (the starting hoard level), then do not perform this step
      *     - a third step might be necessary if we account for the fact that some universal upgrades affect production speed
      * 2. multiply the hoard's gold conversion rate by the conversion rate multiplier (in most cases, it will be 1)
      * 3. decrement # of hoard items in the current hoard
      *     -I think it's hoard items. If these upgrades cost gold, you will have to pass me updated gold as well
      * 4. set hoardUpgrade unlocked = true
      * 
      * What I get passed:
      * 1. hoardId
      * 2. hoard productionSpeed
      * 3. hoard goldConversionRate
      * 4. upgradeId
      * 5. upgrade's unlocked boolean value (though I could probably just assume this as True) 
      * 
      */
    def upgradeHoard(userid:Int,  hoardType:Int, newSpeed:Double, newConversionRate:Double, upgradeId:Int, unlocked:Boolean):Future[Int] = {
        println("in database upgradeHoard")
        db.run((for {h <- Hoard if h.userId === userid && h.hoardtype === hoardType} yield {h.productionspeed}).update(newSpeed))
        db.run((for {h <- Hoard if h.userId === userid && h.hoardtype === hoardType} yield {h.goldconversionrate}).update(newConversionRate))
        db.run((for {u <- Hoardupgrade if u.hoardupgradeId === upgradeId} yield {u.unlocked}).update(unlocked))
        Future.successful(1)
    }

    /** Process:
      * 1. increase hoardLevel by 1
      * 2. multiply production speed of hoard by 1.1
      * 3. multiply hoard cost by 1.35
      * 4. decrement gold based on how much it costs to level a hoard up
      * 
      * What I get passed:
      * 1. hoardId
      * 2. hoardLevel
      * 3. productionSpeed
      * 4. hoardCost
      * 5. user's gold
      */
    def levelUpHoard(userid:Int, hoardId:Int, hoardLv:Int, newSpeed:Double, newCost:Int, newGold:Int):Future[Int] = {
        Future.successful(1)
    }

    /** Process:
      * 1. set unlocked to true on the next hoard up
      * 2. decrement gold based on how much it costs to buy the new hoard
      * 
      * What I get passed:
      * 1. hoardId
      *     - you might not have info about the next hoard up since I don't pass info on non-unlocked hoardes
      *     - therefore you might just have to pass me the current highest hoard+1 for the hoardtype(below)
      *     - then just call a getHoardInfo to get the new shit
      * 2. hoardType
      * 3. unlocked of new hoard (can also probably assume true)
      * 4. user's gold
      * 
      */
    def unlockNewHoard(userid:Int, hoardType:Int, newUnlocked:Boolean, newGold:Int):Future[Int] = {
        db.run((for { u <- Users if u.id === userid} yield {u.gold}).update(newGold))
        db.run((for {h <- Hoard if h.userId === userid && h.hoardtype === hoardType} yield {h.unlocked}).update(newUnlocked))
        Future.successful(1)
    }


    def loadUserInfo(username:String, userid:Int, info:String):Future[Int] = Future.successful(1)
    
    def loadStealingInfo(username:String, userid:Int, info:String):Future[Int] = Future.successful(1)

    

    //this is going to be the exact opposite of create user
    //need to figure out how to drop members of a db in code
    def resetAll(userid:Int):Future[Boolean] = Future.successful(true)


    /**TO DO:
     * 
     * make hoard cost into a double
     * make gold a double
     * 
     * Test validateUser
     * Test createUser
     * Test getUserInfo
     * Test getHoardUpgrades
     * Test getHoardInfo
     * Test getStealingInfo
     * Implement all other unimplemented functions
     * 
     */  
}
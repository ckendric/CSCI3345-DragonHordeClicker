package models

import play.api.libs.json.Json

case class UserData(username: String, password: String)
case class UserInfoData(username: String, gold: Double, hordes: List[String])
case class GoldData(username: String, gold: Int)
case class HordeData(username: String, hordeName: String, items: Int)
case class User(username: String)
case class StealData(username: String, victim: String)

object ReadsAndWrites {
    implicit val UserReads = Json.reads[User]
    implicit val UserWrites = Json.writes[User]
    implicit val UserInfoDataReads = Json.reads[UserInfoData]
    implicit val UserInfoDataWrites = Json.writes[UserInfoData]
    implicit val HordeDataReads = Json.reads[HordeData]
    implicit val HordeDataWrites = Json.writes[HordeData]
    implicit val UserDataReads = Json.reads[UserData]
    implicit val UserDataWrites = Json.writes[UserData]
    implicit val StealDataWrites = Json.writes[StealData]
    implicit val StealDataReads = Json.reads[StealData]
    implicit val GoldDataWrites = Json.writes[GoldData]
    implicit val GoldDataReads = Json.reads[GoldData]
}
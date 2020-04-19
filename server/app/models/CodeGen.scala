package models

object CodeGen extends App {
    slick.codegen.SourceCodeGenerator.run(
        "slick.jdbc.PostgresProfile",
        "org.postgresql.Driver",
        "jdbc:postgresql://localhost/dragonhordes?user=mlewis&password=password",
        "C:/Users/Quentin Morris/Desktop/Everything/College/Sem6/WebApps/CSCI3345-DragonHordeClicker/server/app/",
        "models", None, None, true, false
    )
}
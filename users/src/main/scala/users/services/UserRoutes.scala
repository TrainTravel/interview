package users.services

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

class UserRoutes() {
  val userRoutes = pathPrefix("companies")(getUser)

  def getUser: Route = get {???}
}

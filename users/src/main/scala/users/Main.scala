package users

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshaller
import cats.data._
import cats.implicits._
import io.circe.generic.auto._
import io.circe.Json
import io.circe.syntax._
import users.domain.User._
import users.config._
import users.main._
import users.services.UserRoutes

import scala.io.StdIn
import scala.util.{Failure, Success}

object Main extends App {
  implicit val system = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext = system.executionContext

  val config = ApplicationConfig(
    executors = ExecutorsConfig(
      services = ExecutorsConfig.ServicesConfig(
        parallellism = 4
      )
    ),
    services = ServicesConfig(
      users = ServicesConfig.UsersConfig(
        failureProbability = 0.1,
        timeoutProbability = 0.1
      )
    )
  )

  val application = Application.fromApplicationConfig.run(config)
  val service = application.services.userManagement
//  lazy val routes = new UserRoutes().userRoutes
  lazy val routes = pathPrefix("users")(getUser)

  def getUser: Route = get {
    path(Segment) { id =>
      onComplete(service.get(Id(id))) {
        case Success(Right(user)) => complete
        case Failure(exception) => ???
      }
    }
  }


  val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes)

  println(s"Server now online. Please navigate to http://localhost:8080/hello\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return

  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}

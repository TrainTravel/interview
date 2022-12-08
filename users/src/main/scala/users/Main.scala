package users

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.circe.syntax._
import users.services.usermanagement.Error
import users.domain.User._
import users.config._
import users.domain.{EmailAddress, Password, User, UserName}
import users.main._

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

  case class CreateUser(userName: String, emailAddress: String, password: Option[String])

  val routes = pathPrefix("users") {
    get {
      pathEndOrSingleSlash {
        onComplete(service.all()) {
          case Success(Right(users)) => complete(users)
          case Success(Left(ex))  => complete(ex)
          case Failure(ex) => complete(ex)
        }
      } ~
      (path(Segment)) { id =>
        onComplete(service.get(Id(id))) {
          case Success(Right(user)) =>
            println(user.asJson)
            complete(200, user.asJson)
          case Success(Left(Error.NotFound)) => complete(s"Failed to get user by that id: id not found")
          case Failure(ex) => complete(s"Failed to get user by that id: $ex")
        }
      }
    } ~
    post {
      entity(as[CreateUser]) { req =>
        onComplete(
          service
            // Try[Either[Error, User]]
            .signUp(
              UserName(req.userName),
              EmailAddress(req.emailAddress),
              req.password.map(Password)
            )
        ) {
          case Success(Right(user)) => complete((StatusCodes.Created, s"User ${user.userName} successfully created!"))
          case Success(Left(Error.Exists)) => complete((StatusCodes.Conflict, "Sorry, this username is already taken, please choose another username."))
          case Success(Left(e)) => complete((StatusCodes.BadRequest, e))
          case Failure(e) => complete((StatusCodes.BadRequest, e))
          }
        }
    }

  }

  val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes)

  println(s"Server now online. Please navigate to http://localhost:8080\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return

  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}

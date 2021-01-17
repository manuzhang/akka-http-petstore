package io.github.manuzhang.petstore

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.github.swagger.akka.SwaggerSite
import fr.davit.akka.http.metrics.core.HttpMetrics.enrichHttp
import fr.davit.akka.http.metrics.core.HttpMetricsRegistry
import fr.davit.akka.http.metrics.core.scaladsl.server.HttpMetricsDirectives.pathLabeled
import fr.davit.akka.http.metrics.prometheus.{PrometheusRegistry, PrometheusSettings}
import io.github.manuzhang.petstore.controller.OrderController
import io.prometheus.client.CollectorRegistry
import org.rogach.scallop.ScallopConf
import org.slf4j.{Logger, LoggerFactory}
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

object Service extends SwaggerSite {

  private val LOG: Logger = LoggerFactory.getLogger(Service.getClass)

  implicit val system = ActorSystem("petstore")
  implicit val executionContext = system.dispatcher

  implicit def myExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case e: Exception =>
        e.printStackTrace()
        complete(HttpResponse(StatusCodes.InternalServerError, entity = e.toString))
    }

  class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
    val port = opt[Int](required = false, default = Some(8080))
    verify()
  }

  def main(args: Array[String]): Unit = {
    val conf = new Conf(args)
    run(conf.port())
  }

  def hello: Route = get {
    pathLabeled(PathEnd) {
      complete(StatusCodes.OK, s"Hi")
    }
  }

  def swagger: Route = {
    concat(
      // this path is required by swaggerSiteRoute
      path("api-docs" / "swagger.json") {
        getFromResource("openapi.yaml")
      },
      swaggerSiteRoute
    )
  }

  def run(port: Int): Unit = {
    val metricsRegistry = prometheusRegistry
    val route = cors() {
      handleExceptions(myExceptionHandler) {
        concat(
          hello,
          swagger,
          pathPrefix("v3") {
            OrderController.route
          }
        )
      }
    }

    startHttpServer(port, route, metricsRegistry)
    Await.result(system.whenTerminated, Duration.Inf)
  }

  private def startHttpServer(port: Int, routes: Route, registry: HttpMetricsRegistry)
    (implicit system: ActorSystem): Unit = {
    val futureBinding = Http().newMeteredServerAt("0.0.0.0", port, registry).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        LOG.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        LOG.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

  private def prometheusRegistry: PrometheusRegistry = {
    val prometheus = new CollectorRegistry
    val settings = PrometheusSettings.default
      .withIncludeMethodDimension(true)
      .withIncludePathDimension(true)
      .withIncludeStatusDimension(true)
    PrometheusRegistry(prometheus, settings)
  }
}

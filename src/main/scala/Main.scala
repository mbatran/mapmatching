import java.io.{BufferedWriter, FileWriter}
import java.util.Locale

import com.graphhopper.reader.osm.GraphHopperOSM
import com.graphhopper.routing.util.EncodingManager
import com.graphhopper.{GHRequest, GraphHopper}

object Main extends App {


  val hopper: GraphHopper = new GraphHopperOSM().forServer
  val graphFolder = "C:\\Users\\Batran\\Documents\\Work\\routing\\graphFolderChina"
  hopper.setGraphHopperLocation(graphFolder)
  hopper.setEncodingManager(new EncodingManager("car"))
  hopper.importOrLoad()

  val bufferedSource = io.Source.fromFile("./traj.csv")
  val writer = new BufferedWriter(new FileWriter(("wkt.csv")))
  var latFrom = 0.0
  var lonFrom = 0.0
  var latTo = 0.0
  var lonTo = 0.0

  for (line <- bufferedSource.getLines) {
    val cols = line.split(",").map(_.trim)
    latTo = cols(0).toDouble
    lonTo = cols(1).toDouble

    println(latTo)
    if (latFrom != 0) {
      val req = new GHRequest(latFrom, lonFrom, latTo, lonTo).setWeighting("fastest").
        setVehicle("car").
        setLocale(Locale.US)

      val rsp = hopper.route(req)
      if (rsp.hasErrors) { // handle them!
        rsp.getErrors()
      } else {
        val path = rsp.getBest
        val distance = path.getDistance
        println(distance)
      }
    }
  }
}
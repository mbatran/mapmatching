import java.text.SimpleDateFormat
import java.util
import java.util._
import com.graphhopper.matching.MapMatching
import com.graphhopper.routing.weighting.FastestWeighting
import com.graphhopper.util.Parameters
import com.graphhopper.reader.osm.GraphHopperOSM
import com.graphhopper.routing.AlgorithmOptions
import com.graphhopper.routing.util.{EncodingManager, HintsMap}
import com.graphhopper.util.GPXEntry
import com.graphhopper.{GHRequest, GraphHopper}
import com.graphhopper.routing.util.CarFlagEncoder

object Matching extends App {


  val hopper: GraphHopper = new GraphHopperOSM()
  hopper.setDataReaderFile("C:\\Users\\Batran\\Downloads\\china-latest.osm.pbf")
  val graphFolder = "chinaGraph"
  hopper.setGraphHopperLocation(graphFolder)
  val encoder = new CarFlagEncoder
  hopper.setEncodingManager(new EncodingManager(encoder))
  hopper.getCHFactoryDecorator.setEnabled(false)
  hopper.importOrLoad()



  var gpxList: List[GPXEntry]=  new util.ArrayList[GPXEntry]()
  val bufferedSource = io.Source.fromFile("./traj.csv")
  val sdf=new SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
  for (line <- bufferedSource.getLines) {
    val cols = line.split(",").map(_.trim)
    val lat = cols(0).toDouble
    val lon = cols(1).toDouble
    val time=  sdf.parse(cols(2)).getTime
    val gpx=new GPXEntry(lat,lon,time)
    gpxList.add(gpx)
  }

  val algorithm = Parameters.Algorithms.DIJKSTRA_BI
  val weighting = new FastestWeighting(encoder)
  val algoOptions = new AlgorithmOptions(algorithm, weighting)
  val mapMatching = new MapMatching(hopper, algoOptions)
  val mr = mapMatching.doWork(gpxList)
  val matches = mr.getEdgeMatches
  println(matches.get(0).getGpxExtensions)
}

//https://libraries.io/github/graphhopper/map-matching
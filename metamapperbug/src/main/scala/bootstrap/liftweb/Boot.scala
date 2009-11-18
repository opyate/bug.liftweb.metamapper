package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.common._
import _root_.java.sql._
import metamapperbug.model._

/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  
  def modelList = List[BaseMetaMapper](Beeblebrox)
  
  def boot {
    DB.addLogFunc { 
      case (query, time) => { 
         Log.info("All queries took " + time + "ms: ") 
         query.allEntries.foreach({ case DBLogEntry(stmt, duration) => 
            Log.info(stmt + " took " + duration + "ms")}) 
            Log.info("End queries") 
      } 
    } 
    
    if (!DB.jndiJdbcConnAvailable_?) {
      DB.defineConnectionManager(DefaultConnectionIdentifier, DerbyDBVendor)
      Schemifier.schemify(true, Log.infoF _, DefaultConnectionIdentifier, modelList :_*)
    }
    
    
    // where to search snippet
    LiftRules.addToPackages("metamapperbug")

    // Build SiteMap
    val entries = Menu(Loc("Home", List("index"), "Home")) :: Nil
    LiftRules.setSiteMap(SiteMap(entries:_*))
  }
}

object DerbyDBVendor extends ConnectionManager {
  def newConnection(name: ConnectionIdentifier): Box[Connection] = {
    try {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver")
      val dm =  DriverManager.getConnection("jdbc:derby:hitchhiker;create=true")

      Full(dm)
    } catch {
      case e : Exception => e.printStackTrace; Empty
    }
  }
  def releaseConnection(conn: Connection) {conn.close}
}
package org.insightedge.examples.financialengineering.model

import org.insightedge.examples.financialengineering.CoreSettings
import org.insightedge.scala.annotation.SpaceId

import scala.beans.BeanProperty

/** User: jason
  *
  * Time: 4:07 AM
  * An Investment that occurred in the past.
  */
case class Investment(@SpaceId(autoGenerate = true)
                      @BeanProperty
                      var id: String,
                      @BeanProperty
                      var buyPrice: Double,
                      @BeanProperty
                      var sellPrice: Double,
                      @BeanProperty
                      var startDateMs: Long, // tickTime - 1 mo
                      @BeanProperty
                      var endDateMs: Long, // tickTime
                      @BeanProperty
                      var buyCost: Double = 0,
                      @BeanProperty
                      var sellCost: Double = 0,
                      @BeanProperty
                      var dividendsDuringTerm: Double = 0,
                      @BeanProperty
                      var exchange: String = "NYSE") {
  def this() = this(null, -1, -1, -1, -1, -1, -1, -1, null)
}

object Investment {
  def apply(d: TickData, d2: TickData) = {
    val tickPair = if (d2.timestampMs > d.timestampMs) (d2, d) else (d, d2)
    
    new Investment(null,
      buyPrice = tickPair._1.close,
      sellPrice = tickPair._2.open,
      startDateMs = tickPair._1.timestampMs,
      endDateMs = tickPair._2.timestampMs
    )
  }
}

object InvestmentHelp {

  import java.time.Duration

  implicit class Help(i: Investment) {

    def duration(): Duration = {
      // 1440000 / 518400000
      Duration.ofMillis(i.endDateMs - i.startDateMs)
    }

    def years(): Double = {
      (i.endDateMs - i.startDateMs) / (CoreSettings.daysPerYear * CoreSettings.msPerDay.toDouble)
    }

    def compoundAnnualGrowthRate(): Double = {
      math.pow((i.sellPrice + i.dividendsDuringTerm - i.buyCost - i.sellCost) / i.buyPrice, 1 / years()) - 1
    }

    def CAGR(): Double = {
      compoundAnnualGrowthRate()
    }

  }
}
/*
 * © 2017. Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc.,
 * Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made
 * available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled
 * without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */

package com.stratio.performance.tests

import com.stratio.performance.common.Common
import com.stratio.performance.configurations._
import io.gatling.core.Predef._
import io.gatling.core.body.Body
import io.gatling.http.Predef._

class Perf150Users30MinutesHighActivityPostgreSQL extends Simulation with Common with Test_150Users
    with Test_30MinutesDuration with Test_Concurrent15SecondsUsersIncrement with Test_HighLoad
    with Test_PostgreSQLQuery {

  val executionName: String = "Launch PostgreSQL QUERY (150 users - 30 minutes - high activity)"

  val scenarioName: String = "Scenario_" + this.getClass.getSimpleName

  val httpProtocolBuilder = http
    .baseURL(baseURL)
    .acceptHeader(acceptHeader)
    .acceptCharsetHeader(acceptCharsetHeader)
    .acceptEncodingHeader(acceptEncodingHeader)
    .contentTypeHeader(contentTypeHeader)

  val commonHeaders = Map(
    "Cookie" -> cookie)

  val body: Body = StringBody(query)

  val start = System.currentTimeMillis

  val scenarioBuilder = scenario(scenarioName)
    .during(maxDuration) {
      repeat(numRepetitionsByUser) {
        doIf(session => {(System.currentTimeMillis - start) < maxDuration.toMillis}) {
          exec(
            http(executionName)
              .post(queryEndpoint)
              .body(body)
              .headers(commonHeaders)
              .check(status.is(checkStatus))
          ).pause(pauseTime)
        }
      }
    }

  setUp(
    scenarioBuilder.inject(rampUsers(maxUsers) over usersRampUpTime)
  ).protocols(httpProtocolBuilder)

}

package io.hhplus.concertreservationservice

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.AssertionMode

object ProjectTestConfig : AbstractProjectConfig() {
    override val assertionMode = AssertionMode.Warn
    override val isolationMode = IsolationMode.InstancePerLeaf
}

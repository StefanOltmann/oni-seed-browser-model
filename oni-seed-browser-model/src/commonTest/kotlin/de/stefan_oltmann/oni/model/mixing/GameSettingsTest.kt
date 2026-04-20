/*
 * ONI Seed Browser
 * Copyright (C) 2026 Stefan Oltmann
 * https://stefan-oltmann.de
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.stefan_oltmann.oni.model.mixing

import de.stefan_oltmann.oni.model.AsteroidType
import de.stefan_oltmann.oni.model.Dlc
import de.stefan_oltmann.oni.model.ZoneType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GameSettingsTest {

    @Test
    fun testGetActiveMixingsNoneActive() {

        val gameSettings = GameSettings()

        for (item in MixingItem.entries)
            gameSettings.setMixingLevel(item, MixingLevel.DISABLED)

        assertTrue(gameSettings.getActiveMixings().isEmpty())
    }

    @Test
    fun testGetActiveMixingsSomeActive() {

        val gameSettings = GameSettings()

        gameSettings.setMixingLevel(MixingItem.SugarWoods, MixingLevel.ENABLED)
        gameSettings.setMixingLevel(MixingItem.Raptor, MixingLevel.GUARANTEED)

        val activeMixings = gameSettings.getActiveMixings()

        val activeDlcs = gameSettings.getActiveMixings()

        assertEquals(
            expected = listOf(
                MixingItem.SugarWoods,
                MixingItem.Raptor
            ),
            actual = activeDlcs
        )
    }

    @Test
    fun testGetActiveDlcsNoneActive() {

        val gameSettings = GameSettings()

        for (item in MixingItem.entries)
            gameSettings.setMixingLevel(item, MixingLevel.DISABLED)

        assertTrue(gameSettings.getActiveDlcs().isEmpty())
    }

    @Test
    fun testGetActiveDlcsSomeActive() {

        val gameSettings = GameSettings()

        gameSettings.setMixingLevel(MixingItem.DlcFrostyPlanet, MixingLevel.ENABLED)
        gameSettings.setMixingLevel(MixingItem.DlcBionicBooster, MixingLevel.ENABLED)
        gameSettings.setMixingLevel(MixingItem.DlcPrehistoricPlanet, MixingLevel.DISABLED)

        val activeDlcs = gameSettings.getActiveDlcs()

        assertEquals(
            expected = listOf(
                Dlc.FrostyPlanet,
                Dlc.BionicBooster
            ),
            actual = activeDlcs
        )
    }

    @Test
    fun testGetActiveAsteroidsSomeActive() {

        val gameSettings = GameSettings()

        gameSettings.setMixingLevel(MixingItem.CeresAsteroid, MixingLevel.ENABLED)
        gameSettings.setMixingLevel(MixingItem.PrehistoricAsteroid, MixingLevel.DISABLED)

        val activeAsteroidTypes = gameSettings.getActiveAsteroidTypes()

        assertEquals(
            expected = listOf(
                AsteroidType.MixingCeresAsteroid
            ),
            actual = activeAsteroidTypes
        )
    }

    @Test
    fun testGetActiveZoneTypesSomeActive() {

        val gameSettings = GameSettings()

        gameSettings.setMixingLevel(MixingItem.IceCaves, MixingLevel.ENABLED)
        gameSettings.setMixingLevel(MixingItem.CarrotQuarry, MixingLevel.GUARANTEED)

        val activeZoneTypes = gameSettings.getActiveZoneTypes()

        assertEquals(
            expected = listOf(
                ZoneType.IceCaves,
                ZoneType.CarrotQuarry
            ),
            actual = activeZoneTypes
        )
    }

    @Test
    fun testAllMixingsDisabled() {

        val gameSettings = GameSettings()

        for (item in MixingItem.entries)
            gameSettings.setMixingLevel(item, MixingLevel.DISABLED)

        assertEquals(
            expected = "0",
            actual = gameSettings.getMixingSettingsCode()
        )
    }

    /**
     * All DLC activated, but none of the remixes.
     */
    @Test
    fun testAllDlcActivated() {

        val gameSettings = GameSettings()

        for (item in MixingItem.entries)
            gameSettings.setMixingLevel(item, MixingLevel.DISABLED)

        gameSettings.setMixingLevel(MixingItem.DlcFrostyPlanet, MixingLevel.ENABLED)
        gameSettings.setMixingLevel(MixingItem.DlcBionicBooster, MixingLevel.ENABLED)
        gameSettings.setMixingLevel(MixingItem.DlcPrehistoricPlanet, MixingLevel.ENABLED)

        assertEquals(
            expected = "J3ET5",
            actual = gameSettings.getMixingSettingsCode()
        )
    }

    /**
     * All DLC activated and all the mixings.
     */
    @Test
    fun testAllMixingsGuaranteed() {

        val gameSettings = GameSettings()

        for (item in MixingItem.entries) {

            if (item is MixingItem.DlcMixingItem)
                gameSettings.setMixingLevel(item, MixingLevel.ENABLED)
            else
                gameSettings.setMixingLevel(item, MixingLevel.GUARANTEED)
        }

        assertEquals(
            expected = "VWVP8",
            actual = gameSettings.getMixingSettingsCode()
        )
    }

    @Test
    fun testAllMixingsLikely() {

        val gameSettings = GameSettings()

        for (item in MixingItem.entries)
            gameSettings.setMixingLevel(item, MixingLevel.ENABLED)

        assertEquals(
            expected = "70N97",
            actual = gameSettings.getMixingSettingsCode()
        )
    }

    @Test
    fun testFromRemixCodeAllDisabled() {

        val parsed = GameSettings.fromRemixCode("0")

        for (item in MixingItem.entries)
            assertEquals(MixingLevel.DISABLED, parsed.getMixingLevel(item))
    }

    @Test
    fun testFromRemixCodeAllDlcActivated() {

        val parsed = GameSettings.fromRemixCode("J3ET5")

        for (item in MixingItem.entries) {
            val expected = when (item) {
                MixingItem.DlcFrostyPlanet,
                MixingItem.DlcBionicBooster,
                MixingItem.DlcPrehistoricPlanet -> MixingLevel.ENABLED

                else -> MixingLevel.DISABLED
            }
            assertEquals(expected, parsed.getMixingLevel(item))
        }
    }

    @Test
    fun testFromRemixCodeAllMixingsGuaranteed() {

        val parsed = GameSettings.fromRemixCode("VWVP8")

        for (item in MixingItem.entries) {
            val expected = when {
                item is MixingItem.DlcMixingItem -> MixingLevel.ENABLED
                else -> MixingLevel.GUARANTEED
            }
            assertEquals(expected, parsed.getMixingLevel(item))
        }
    }

    @Test
    fun testFromRemixCodeAllMixingsLikely() {

        val parsed = GameSettings.fromRemixCode("70N97")

        for (item in MixingItem.entries)
            assertEquals(MixingLevel.ENABLED, parsed.getMixingLevel(item))
    }

    /**
     * This test ensures that the parsing of the mixing settings codes is always possible.
     */
    @Test
    fun testFuzzingParseMixingSettingsCode() {

        repeat(1000) {

            val original = GameSettings()

            for (item in MixingItem.entries) {

                val randomLevel = MixingLevel.entries.random()

                original.setMixingLevel(item, randomLevel)
            }

            val code = original.getMixingSettingsCode()

            val parsed = GameSettings.fromRemixCode(code)

            for (item in MixingItem.entries) {
                assertEquals(
                    expected = original.getMixingLevel(item),
                    actual = parsed.getMixingLevel(item),
                    message = "Mismatch for $item with code $code"
                )
            }
        }
    }
}

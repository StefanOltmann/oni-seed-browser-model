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
package de.stefan_oltmann.oni.model.filter

import de.stefan_oltmann.oni.model.AsteroidType
import de.stefan_oltmann.oni.model.ClusterType
import de.stefan_oltmann.oni.model.Dlc
import de.stefan_oltmann.oni.model.GameModeType
import de.stefan_oltmann.oni.model.GeyserType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FilterQueryTest {

    @Test
    fun testEmpty() {

        val query = FilterQuery.EMPTY

        assertNull(query.cluster)
        assertEquals(listOf(Dlc.BaseGame), query.dlcs)
        assertEquals(GameModeType.BASEGAME_STANDARD, query.mode)
        assertNull(query.remix)
        assertTrue(query.rules.isEmpty())
    }

    @Test
    fun testCleanCopy_removesEmptyRules() {

        val query = FilterQuery(
            cluster = ClusterType.BASE_TERRA,
            dlcs = listOf(Dlc.BaseGame),
            mode = GameModeType.BASEGAME_STANDARD,
            remix = null,
            rules = listOf(
                listOf(
                    FilterRule(
                        asteroid = AsteroidType.SandstoneDefault,
                        geyserCount = FilterItemGeyserCount(
                            geyser = GeyserType.HYDROGEN,
                            condition = FilterCondition.AT_LEAST,
                            count = 5
                        )
                    )
                ),
                listOf(
                    FilterRule(
                        asteroid = AsteroidType.SandstoneDefault,
                        geyserCount = FilterItemGeyserCount(
                            geyser = GeyserType.COOL_STEAM,
                            condition = FilterCondition.AT_LEAST,
                            count = null
                        )
                    )
                )
            )
        )

        val cleaned = query.cleanCopy()

        assertEquals(2, cleaned.rules.size)
        assertEquals(1, cleaned.rules[0].size)
        assertEquals(0, cleaned.rules[1].size)
    }

    @Test
    fun testSetAsteroid() {

        val query = FilterQuery(
            cluster = ClusterType.BASE_TERRA,
            dlcs = listOf(Dlc.BaseGame),
            mode = GameModeType.BASEGAME_STANDARD,
            remix = null,
            rules = listOf(
                listOf(
                    FilterRule(asteroid = AsteroidType.SandstoneDefault)
                )
            )
        )

        val updated = query.setAsteroid(0, 0, AsteroidType.SandstoneDefault)

        assertEquals(AsteroidType.SandstoneDefault, updated.rules[0][0].asteroid)
    }

    @Test
    fun testSetFilterItem() {

        val query = FilterQuery(
            cluster = ClusterType.BASE_TERRA,
            dlcs = listOf(Dlc.BaseGame),
            mode = GameModeType.BASEGAME_STANDARD,
            remix = null,
            rules = listOf(
                listOf(
                    FilterRule(asteroid = AsteroidType.SandstoneDefault)
                )
            )
        )

        val filterItem = FilterItemGeyserCount(
            geyser = GeyserType.HYDROGEN,
            condition = FilterCondition.AT_LEAST,
            count = 5
        )

        val updated = query.setFilterItem(0, 0, filterItem)

        assertEquals(filterItem, updated.rules[0][0].geyserCount)
    }

    @Test
    fun testSwitchCondition() {

        val query = FilterQuery(
            cluster = ClusterType.BASE_TERRA,
            dlcs = listOf(Dlc.BaseGame),
            mode = GameModeType.BASEGAME_STANDARD,
            remix = null,
            rules = listOf(
                listOf(
                    FilterRule(
                        asteroid = AsteroidType.SandstoneDefault,
                        geyserCount = FilterItemGeyserCount(
                            geyser = GeyserType.HYDROGEN,
                            condition = FilterCondition.AT_LEAST,
                            count = 5
                        )
                    )
                )
            )
        )

        val updated = query.switchCondition(0, 0)

        assertEquals(
            FilterCondition.AT_MOST,
            updated.rules[0][0].geyserCount?.condition
        )
    }

    @Test
    fun testSetValue() {

        val query = FilterQuery(
            cluster = ClusterType.BASE_TERRA,
            dlcs = listOf(Dlc.BaseGame),
            mode = GameModeType.BASEGAME_STANDARD,
            remix = null,
            rules = listOf(
                listOf(
                    FilterRule(
                        asteroid = AsteroidType.SandstoneDefault,
                        geyserCount = FilterItemGeyserCount(
                            geyser = GeyserType.HYDROGEN,
                            condition = FilterCondition.AT_LEAST,
                            count = null
                        )
                    )
                )
            )
        )

        val updated = query.setValue(0, 0, 10)

        assertEquals(10, updated.rules[0][0].geyserCount?.count)
    }

    @Test
    fun testAddEmptyAndRule_withCluster() {

        val query = FilterQuery(
            cluster = ClusterType.BASE_TERRA,
            dlcs = listOf(Dlc.BaseGame),
            mode = GameModeType.BASEGAME_STANDARD,
            remix = null,
            rules = emptyList()
        )

        val updated = query.addEmptyAndRule()

        assertEquals(1, updated.rules.size)
        assertEquals(
            AsteroidType.SandstoneDefault,
            updated.rules[0][0].asteroid
        )
    }

    @Test
    fun testAddEmptyAndRule_withoutCluster() {

        val query = FilterQuery.EMPTY

        val updated = query.addEmptyAndRule()

        assertTrue(updated.rules.isEmpty())
    }

    @Test
    fun testAddEmptyOrRule_withCluster() {

        val query = FilterQuery(
            cluster = ClusterType.BASE_TERRA,
            dlcs = listOf(Dlc.BaseGame),
            mode = GameModeType.BASEGAME_STANDARD,
            remix = null,
            rules = listOf(
                listOf(
                    FilterRule(asteroid = AsteroidType.SandstoneDefault)
                )
            )
        )

        val updated = query.addEmptyOrRule(0)

        assertEquals(2, updated.rules[0].size)
    }

    @Test
    fun testRemoveRule() {

        val query = FilterQuery(
            cluster = ClusterType.BASE_TERRA,
            dlcs = listOf(Dlc.BaseGame),
            mode = GameModeType.BASEGAME_STANDARD,
            remix = null,
            rules = listOf(
                listOf(
                    FilterRule(asteroid = AsteroidType.SandstoneDefault),
                    FilterRule(asteroid = AsteroidType.SandstoneDefault)
                )
            )
        )

        val updated = query.removeRule(0, 0)

        assertEquals(1, updated.rules[0].size)
    }

    @Test
    fun testRemoveRule_removesEmptyList() {

        val query = FilterQuery(
            cluster = ClusterType.BASE_TERRA,
            dlcs = listOf(Dlc.BaseGame),
            mode = GameModeType.BASEGAME_STANDARD,
            remix = null,
            rules = listOf(
                listOf(
                    FilterRule(asteroid = AsteroidType.SandstoneDefault)
                )
            )
        )

        val updated = query.removeRule(0, 0)

        assertTrue(updated.rules.isEmpty())
    }
}

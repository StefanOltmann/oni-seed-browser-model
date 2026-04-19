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

class GameSettings {

    private val mixingLevels: MutableMap<MixingItem, MixingLevel> =
        MixingItem.entries.associateWith { MixingLevel.DISABLED }.toMutableMap()

    fun getMixingLevel(item: MixingItem): MixingLevel =
        mixingLevels[item] ?: MixingLevel.DISABLED

    fun setMixingLevel(item: MixingItem, level: MixingLevel) {

        require(item in MixingItem.entries) {
            "Invalid Mixing Item: $item not in ${MixingItem.entries.joinToString()}"
        }

        mixingLevels[item] = level
    }

    fun getActiveMixings(): List<MixingItem> =
        MixingItem.entries.filter { mixingLevels[it] != MixingLevel.DISABLED }

    fun getActiveDlcs(): List<Dlc> =
        getActiveMixings()
            .filterIsInstance<MixingItem.DlcMixingItem>()
            .map { it.dlc }

    fun getActiveAsteroidTypes(): List<AsteroidType> =
        getActiveMixings()
            .filterIsInstance<MixingItem.AsteroidMixingItem>()
            .map { it.asteroidType }

    fun getActiveZoneTypes(): List<ZoneType> =
        getActiveMixings()
            .filterIsInstance<MixingItem.ZoneTypeMixingItem>()
            .map { it.zoneType }

    fun getMixingSettingsCode(): String {

        var input = 0

        for (mixingItem in MixingItem.entries) {

            val currentLevel = mixingLevels[mixingItem] ?: MixingLevel.DISABLED

            input = input * COORDINATE_RANGE + currentLevel.coordinateValue
        }

        return binaryToBase36(input)
    }

    companion object {

        private const val BASE36_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val BASE36_BASE = 36

        /**
         * Coordinate range for all mixing items so far was 5,
         * so we use this constant for simplicity.
         */
        private const val COORDINATE_RANGE = 5

        fun fromRemixCode(code: String): GameSettings {

            var binary: Int = base36ToBinary(code.uppercase())

            val gameSettings = GameSettings()

            for (index in MixingItem.entries.size - 1 downTo 0) {

                val mixingItem = MixingItem.entries[index]

                val coordinateValue = (binary % COORDINATE_RANGE)

                require(coordinateValue < mixingItem.levels.size) {
                    "Invalid Mixing String: Level $coordinateValue is not possible for $mixingItem"
                }

                binary /= COORDINATE_RANGE

                gameSettings.setMixingLevel(
                    item = mixingItem,
                    level = mixingItem.getLevel(coordinateValue)
                )
            }

            if (binary != 0)
                error("Invalid Mixing String: bigInteger not 0 after all settings extracted")

            return gameSettings
        }

        private fun binaryToBase36(input: Int): String {

            if (input == 0)
                return "0"

            return buildString {

                var value: Int = input

                while (value > 0) {

                    val remainder = (value % BASE36_BASE)

                    append(BASE36_ALPHABET[remainder])

                    value /= BASE36_BASE
                }
            }
        }

        private fun base36ToBinary(input: String): Int {

            if (input == "0")
                return 0

            var output = 0

            for (index in input.length - 1 downTo 0) {

                val digit = BASE36_ALPHABET.indexOf(input[index])

                output = output * BASE36_BASE + digit
            }

            return output
        }
    }
}

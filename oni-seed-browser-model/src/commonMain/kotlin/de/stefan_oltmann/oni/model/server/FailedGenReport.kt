/*
 * ONI Seed Browser
 * Copyright (C) 2025 Stefan Oltmann
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
package de.stefan_oltmann.oni.model.server

import de.stefan_oltmann.oni.model.ClusterType
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * This is the format expected to be sent from the mod
 */
@Serializable
data class FailedGenReport(

    val userId: String,

    val installationId: String,

    val gameVersion: Int,

    val fileHashes: Map<String, String>,

    val coordinate: String

) {

    val uploadSteamId: String? = if (userId.startsWith("Steam-"))
        userId.drop(6)
    else
        null

    @OptIn(ExperimentalUuidApi::class)
    fun check(
        tokenSteamId: String
    ): FailedGenReportCheckResult {

        /* InstallationId must be valid UUID */
        try {
            Uuid.parse(installationId)
        } catch (_: IllegalArgumentException) {
            return FailedGenReportCheckResult.Error("Invalid 'installationId': $installationId")
        }

        return when {

            /* InstallationId is mandatory */
            installationId.isBlank() ->
                FailedGenReportCheckResult.Error("Missing 'installationId'")

            /* UserId is mandatory */
            userId.isBlank() ->
                FailedGenReportCheckResult.Error("Missing 'userId'")

            /* We only accept the Steam version */
            uploadSteamId == null ->
                FailedGenReportCheckResult.Error("User ID was not Steam: $userId")

            /* Steam ID in upload must match Steam ID in token */
            uploadSteamId != tokenSteamId ->
                FailedGenReportCheckResult.Error("Steam ID mismatch: $uploadSteamId != $tokenSteamId")

            /* Game version must be set */
            gameVersion == 0 ->
                FailedGenReportCheckResult.Error("Missing 'gameVersion'")

            /* File hashes must be set */
            fileHashes.isEmpty() ->
                FailedGenReportCheckResult.Error("Missing 'fileHashes'")

            /* ModHash must be set */
            fileHashes["modHash"].isNullOrBlank() ->
                FailedGenReportCheckResult.Error("Missing 'fileHashes.modHash'")

            /* Coordinate must be set */
            coordinate.isBlank() ->
                FailedGenReportCheckResult.Error("Missing 'cluster.coordinate'")

            /* Coordinate must be valid */
            !ClusterType.isValidCoordinate(coordinate) ->
                FailedGenReportCheckResult.Error("Invalid 'cluster.coordinate': $coordinate")

            /*
             * If none of the issues above are present, the upload is accepted.
             */
            else -> FailedGenReportCheckResult.Okay
        }
    }
}

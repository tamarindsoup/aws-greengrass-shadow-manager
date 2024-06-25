/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.shadowmanager.util;

import com.aws.greengrass.shadowmanager.exception.InvalidRequestParametersException;
import com.aws.greengrass.shadowmanager.model.ShadowRequest;
import com.aws.greengrass.testcommons.testutilities.GGExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static com.aws.greengrass.shadowmanager.TestUtils.INVALID_NAME_LENGTH;
import static com.aws.greengrass.shadowmanager.TestUtils.INVALID_NAME_PATTERN;
import static com.aws.greengrass.shadowmanager.TestUtils.INVALID_RESERVED_SHADOW_NAME;
import static com.aws.greengrass.shadowmanager.TestUtils.RESERVED_SHADOW_NAME;
import static com.aws.greengrass.shadowmanager.TestUtils.SHADOW_NAME;
import static com.aws.greengrass.shadowmanager.TestUtils.THING_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({MockitoExtension.class, GGExtension.class})
class ValidatorTest {

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> invalidShadowRequests() {

        return Stream.of(
                Arguments.of(null, SHADOW_NAME, "ThingName is missing"),
                Arguments.of("", SHADOW_NAME, "ThingName is missing"),
                Arguments.of(INVALID_NAME_LENGTH, SHADOW_NAME, "ThingName has a maximum"),
                Arguments.of(INVALID_NAME_PATTERN, SHADOW_NAME, "ThingName must match"),

                Arguments.of(THING_NAME, INVALID_NAME_LENGTH, "ShadowName has a maximum"),
                Arguments.of(THING_NAME, INVALID_NAME_PATTERN, "ShadowName must match"),
                Arguments.of(THING_NAME, INVALID_RESERVED_SHADOW_NAME, "ShadowName must match")
        );
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> validShadowRequests() {
        return Stream.of(
                Arguments.of(THING_NAME, SHADOW_NAME),
                Arguments.of(THING_NAME, RESERVED_SHADOW_NAME)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidShadowRequests")
    void GIVEN_invalid_shadow_request_WHEN_validate_shadow_request_THEN_throw_invalid_request_parameters_exception(String thingName, String shadowName, String errorMessage) {
        ShadowRequest shadowRequest = new ShadowRequest(thingName, shadowName);
        InvalidRequestParametersException thrown = assertThrows(InvalidRequestParametersException.class, () -> Validator.validateShadowRequest(shadowRequest));
        assertThat(thrown.getMessage(), startsWith(errorMessage));
    }

    @ParameterizedTest
    @MethodSource("validShadowRequests")
    void GIVEN_valid_shadow_request_WHEN_validate_shadow_request_THEN_do_nothing(String thingName, String shadowName) {
        ShadowRequest shadowRequest = new ShadowRequest(thingName, shadowName);
        assertDoesNotThrow(() -> Validator.validateShadowRequest(shadowRequest));
    }
}
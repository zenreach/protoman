/*-
 * -\-\-
 * protoman-validation
 * --
 * Copyright (C) 2016 - 2018 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */

package com.spotify.protoman.validation.rules;

import static com.spotify.protoman.validation.ValidationViolationMatcher.validationViolation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.google.common.collect.ImmutableList;
import com.spotify.protoman.descriptor.DescriptorSet;
import com.spotify.protoman.testutil.DescriptorSetUtils;
import com.spotify.protoman.validation.DefaultSchemaValidator;
import com.spotify.protoman.validation.SchemaValidator;
import com.spotify.protoman.validation.ValidationViolation;
import com.spotify.protoman.validation.ViolationType;
import org.junit.Test;

public class FieldNameChangeRuleTest {

  private final SchemaValidator schemaValidator = DefaultSchemaValidator.builder()
      .addRule(FieldNameChangeRule.create())
      .build();

  @Test
  public void testFieldNameUnchanged() throws Exception {
    final DescriptorSet candidate = DescriptorSetUtils.buildDescriptorSet(
        "foo/bar/a.proto",
        "syntax = 'proto3';\n"
        + "message Herp {\n"
        + "  int32 derp = 1;\n"
        + "}"
    );

    final ImmutableList<ValidationViolation> violations =
        schemaValidator.validate(candidate, candidate);

    assertThat(violations, is(empty()));
  }

  @Test
  public void testFieldNameChanged() throws Exception {
    final DescriptorSet current = DescriptorSetUtils.buildDescriptorSet(
        "foo/bar/a.proto",
        "syntax = 'proto3';\n"
        + "message Herp {\n"
        + "  int32 derp = 1;\n"
        + "}"
    );
    final DescriptorSet candidate = DescriptorSetUtils.buildDescriptorSet(
        "foo/bar/a.proto",
        "syntax = 'proto3';\n"
        + "message Herp {\n"
        + "  int32 herp_a_derp = 1;\n"
        + "}"
    );

    final ImmutableList<ValidationViolation> violations =
        schemaValidator.validate(current, candidate);

    assertThat(
        violations,
        contains(
            validationViolation()
                .type(equalTo(ViolationType.GENERATED_SOURCE_CODE_INCOMPATIBILITY_VIOLATION))
                .description(equalTo("field name changed"))
        )
    );
  }
}
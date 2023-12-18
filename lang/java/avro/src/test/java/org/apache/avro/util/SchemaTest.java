/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.util;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;


@RunWith(Parameterized.class)
public class SchemaTest {
  private Schema expectedResult;
  private Class<? extends Exception> expectedException;
  private String name;
  private String doc;
  private String namespace;
  private boolean isError;
  private List<Schema.Field> fields;

  public SchemaTest(Schema expectedResult, Class<? extends Exception> expectedException, String name, String doc, String namespace, boolean isError, List<Schema.Field> fields){
    this.expectedResult = expectedResult;
    this.expectedException = expectedException;
    this.name = name;
    this.doc = doc;
    this.namespace = namespace;
    this.isError = isError;
    this.fields = fields;
  }

  private enum fieldsType {
    NULL,
    EMPTY,
    ONE,
    TWO,
    POSITION, DUPLICATE;

    public List<Schema.Field> getFields() {
      try {
        switch (this) {
          case NULL:
            return null;
          case EMPTY:
            return new ArrayList<>();
          case ONE:
            return Arrays.asList(new Schema.Field("test", Schema.create(Schema.Type.STRING), "test", null));
          case TWO:
            return Arrays.asList(new Schema.Field("test", Schema.create(Schema.Type.STRING), "test", null), new Schema.Field("test2", Schema.create(Schema.Type.STRING), "test2", null));
          case POSITION:
            Schema.Field field = new Schema.Field("test", Schema.create(Schema.Type.STRING), "test", null);
            Field f = Schema.Field.class.getDeclaredField("position");
            f.setAccessible(true);
            f.set(field, 1);
            return Arrays.asList(field);
          case DUPLICATE:
            return Arrays.asList(new Schema.Field("test", Schema.create(Schema.Type.STRING), "test", null), new Schema.Field("test", Schema.create(Schema.Type.STRING), "test", null));
          default:
            return null;
        }
      } catch (Exception e){
        e.printStackTrace();
      }
      return null;
    }
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data(){
    return Arrays.asList(new Object[][]{
      {null, null, "test", "test", "test", false, fieldsType.ONE.getFields()},
      {null, null, "test", "test", "test", false, fieldsType.TWO.getFields()},
      {null, Exception.class, "", "", "", true, fieldsType.TWO.getFields()},
      {null, null, null, null, null, false, fieldsType.ONE.getFields()}, //Test Fallito (Expected Exception: Exception.class)
      {null, Exception.class, "test", "test", "test", false, fieldsType.NULL.getFields()},
      {null, null, "test", "test", "test", false, fieldsType.EMPTY.getFields()}, //Test Fallito (Expected Exception: Exception.class)

      //Jacoco increment
      {null, Exception.class, "test", "test", "test", false, fieldsType.POSITION.getFields()},

      //Badua increment
      {null, AvroRuntimeException.class, "test", "test", "test", false, fieldsType.DUPLICATE.getFields()},

    });
  }

  @Before
  public void setUp() {

  }

  @Test
  public void createRecordTest(){
    try{
      Schema schema = Schema.createRecord(name, doc, namespace, isError, fields);
      if(expectedException == null)
        assertTrue(true);
      else
        fail();
    } catch (Exception e){
      e.printStackTrace();
      System.out.println(e.getMessage());
      if(expectedException != null)
        assertTrue(expectedException.isInstance(e));
      else
        fail();
    }
  }
}

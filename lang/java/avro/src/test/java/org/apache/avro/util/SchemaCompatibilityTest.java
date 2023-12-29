package org.apache.avro.util;

import org.apache.avro.Schema;
import org.apache.avro.SchemaCompatibility;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class SchemaCompatibilityTest {

private boolean expectedResult;
private Schema reader;
private Schema writer;

  public SchemaCompatibilityTest(boolean expectedResult, Schema reader, Schema writer){
    this.expectedResult = expectedResult;
    this.reader = reader;
    this.writer = writer;
  }

  private enum SchemaType {
    INT,
    STRING,
    RECORD;


    public Schema getSchema() {
      switch (this) {
        case INT:
          return Schema.create(Schema.Type.INT);
        case STRING:
          return Schema.create(Schema.Type.STRING);
        case RECORD:
          return Schema.createRecord("test", "test", "test", false);
        default:
          return null;
      }
    }
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
      {true, SchemaType.INT.getSchema(), SchemaType.INT.getSchema()},
      {false, SchemaType.RECORD.getSchema(), SchemaType.INT.getSchema()},
      {false, SchemaType.INT.getSchema(), SchemaType.STRING.getSchema()}
    });
  }

  @Test
  public void checkReaderWriterCompatibilityTest(){
    Schema schema1 = Mockito.mock(Schema.class);
    Schema schema2 = Mockito.mock(Schema.class);
    when(schema1.getType()).thenReturn(Schema.Type.STRING);
    when(schema2.getType()).thenReturn(Schema.Type.STRING);
    SchemaCompatibility.SchemaPairCompatibility c = SchemaCompatibility.checkReaderWriterCompatibility(reader, writer);
    boolean result = SchemaCompatibility.READER_WRITER_COMPATIBLE_MESSAGE.equals(c.getDescription());
    assertEquals(expectedResult, result);
  }
}

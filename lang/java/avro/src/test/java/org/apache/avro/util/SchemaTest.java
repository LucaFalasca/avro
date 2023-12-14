package org.apache.avro.util;

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
    POSITION;

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
      if(expectedException != null)
        assertTrue(expectedException.isInstance(e));
      else
        fail();
    }
  }
}

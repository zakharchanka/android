/*
 * Copyright (C) 2019 The Android Open Source Project
 *
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
 */
package com.android.tools.idea.room.generators;

import static com.android.tools.idea.room.generators.TestUtils.createDatabaseBundle;
import static com.android.tools.idea.room.generators.TestUtils.createEntityBundle;
import static com.android.tools.idea.room.generators.TestUtils.createFieldBundle;
import static com.android.tools.idea.room.generators.TestUtils.createFtsEntityBundle;
import static com.google.common.truth.Truth.assertThat;

import com.android.tools.idea.room.migrations.json.DatabaseBundle;
import com.android.tools.idea.room.migrations.json.DatabaseViewBundle;
import com.android.tools.idea.room.migrations.json.EntityBundle;
import com.android.tools.idea.room.migrations.json.FieldBundle;
import com.android.tools.idea.room.migrations.generators.SqlStatementsGenerator;
import com.android.tools.idea.room.migrations.json.ForeignKeyBundle;
import com.android.tools.idea.room.migrations.json.FtsEntityBundle;
import com.android.tools.idea.room.migrations.json.IndexBundle;
import com.android.tools.idea.room.migrations.json.PrimaryKeyBundle;
import com.android.tools.idea.room.migrations.update.DatabaseUpdate;
import com.android.tools.idea.room.migrations.update.EntityUpdate;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class SqlStatementsGeneratorTest {

  private static void testMigrationStatements(DatabaseBundle oldDatabase, DatabaseBundle newDatabase, String... statements) {
    assertThat(SqlStatementsGenerator.getMigrationStatements(new DatabaseUpdate(oldDatabase, newDatabase)))
      .containsExactly((Object[])statements).inOrder();
  }

  private static void testMigrationStatements(EntityBundle oldEntity, EntityBundle newEntity, String... statements) {
    assertThat(SqlStatementsGenerator.getMigrationStatements(new EntityUpdate(oldEntity, newEntity)))
      .containsExactly((Object[])statements).inOrder();
  }

  private static void testMigrationStatements(EntityUpdate entityUpdate, String... statements) {
    assertThat(SqlStatementsGenerator.getMigrationStatements(entityUpdate))
      .containsExactly((Object[])statements).inOrder();
  }

  @Test
  public void testAddColumn() {
    FieldBundle field = createFieldBundle("column1", "TEXT", null);
    FieldBundle fieldToAdd = createFieldBundle("column2", "TEXT", null);

    testMigrationStatements(createEntityBundle("table", field),
                            createEntityBundle("table", field, fieldToAdd),
                            "ALTER TABLE `table` ADD COLUMN column2 TEXT;");
  }

  @Test
  public void testDeleteColumn() {
    FieldBundle field = createFieldBundle("column1", "TEXT", null);
    FieldBundle fieldToDelete = createFieldBundle("column2", "TEXT", null);

    testMigrationStatements(createEntityBundle("table", field, fieldToDelete),
                            createEntityBundle("table", field),
                            "CREATE TABLE table_data$android_studio_tmp\n" +
                            "(\n" +
                            "\tcolumn1 TEXT,\n" +
                            "\tPRIMARY KEY (column1)\n" +
                            ");",
                            "INSERT INTO table_data$android_studio_tmp (column1)\n" +
                            "\tSELECT column1\n" +
                            "\tFROM `table`;",
                            "DROP TABLE `table`;",
                            "ALTER TABLE table_data$android_studio_tmp RENAME TO `table`;");
  }

  @Test
  public void testAddAndDeleteColumn() {
    FieldBundle field = createFieldBundle("column1", "TEXT", null);
    FieldBundle fieldToDelete = createFieldBundle("column2", "TEXT", null);
    FieldBundle fieldToAdd = createFieldBundle("column3", "TEXT", null);

    testMigrationStatements(createEntityBundle("table", field, fieldToDelete),
                            createEntityBundle("table", field, fieldToAdd),
                            "CREATE TABLE table_data$android_studio_tmp\n" +
                            "(\n" +
                            "\tcolumn1 TEXT,\n" +
                            "\tcolumn3 TEXT,\n" +
                            "\tPRIMARY KEY (column1)\n" +
                            ");",
                            "INSERT INTO table_data$android_studio_tmp (column1)\n" +
                            "\tSELECT column1\n" +
                            "\tFROM `table`;",
                            "DROP TABLE `table`;",
                            "ALTER TABLE table_data$android_studio_tmp RENAME TO `table`;");
  }

  @Test
  public void testModifyColumn() {
    FieldBundle fieldToModify = createFieldBundle("column1", "TEXT", null);
    FieldBundle modifiedField = createFieldBundle("column1", "CHAR", null);
    FieldBundle field = createFieldBundle("column2", "TEXT", null);

    testMigrationStatements(createEntityBundle("table", fieldToModify, field),
                            createEntityBundle("table", modifiedField, field),
                            "CREATE TABLE table_data$android_studio_tmp\n" +
                            "(\n" +
                            "\tcolumn1 CHAR,\n" +
                            "\tcolumn2 TEXT,\n" +
                            "\tPRIMARY KEY (column1)\n" +
                            ");",
                            "INSERT INTO table_data$android_studio_tmp (column1, column2)\n" +
                            "\tSELECT column1, column2\n" +
                            "\tFROM `table`;",
                            "DROP TABLE `table`;",
                            "ALTER TABLE table_data$android_studio_tmp RENAME TO `table`;");
  }

  @Test
  public void testModifyColumnDefaultValue() {
    FieldBundle field = createFieldBundle("column1", "TEXT", null);
    FieldBundle fieldToModify = createFieldBundle("column2", "TEXT", null);
    FieldBundle modifiedField = createFieldBundle("column2", "TEXT", "default");

    testMigrationStatements(createEntityBundle("table", field, fieldToModify),
                            createEntityBundle("table", field, modifiedField),
                            "CREATE TABLE table_data$android_studio_tmp\n" +
                            "(\n" +
                            "\tcolumn1 TEXT,\n" +
                            "\tcolumn2 TEXT DEFAULT 'default',\n" +
                            "\tPRIMARY KEY (column1)\n" +
                            ");",
                            "INSERT INTO table_data$android_studio_tmp (column1, column2)\n" +
                            "\tSELECT column1, column2\n" +
                            "\tFROM `table`;",
                            "DROP TABLE `table`;",
                            "ALTER TABLE table_data$android_studio_tmp RENAME TO `table`;"
    );
  }

  @Test
  public void testAddColumnsWithUserSpecifiedValues() {
    FieldBundle field1 = createFieldBundle("column1", "TEXT", null);
    FieldBundle field2 = createFieldBundle("column2", "TEXT", null);
    FieldBundle field3 = createFieldBundle("column3", "TEXT", null);

    EntityBundle entity = createEntityBundle("table", field1);
    EntityBundle entityWithNewColumnsWithUserSpecifiedValues = createEntityBundle("table", field1, field2, field3);

    EntityUpdate entityUpdate = new EntityUpdate(entity, entityWithNewColumnsWithUserSpecifiedValues);
    entityUpdate.setValuesForUninitializedFields(ImmutableMap.of(field2, "value2", field3, "value3"));

    testMigrationStatements(entityUpdate,
                            "ALTER TABLE `table` ADD COLUMN column3 TEXT;",
                            "ALTER TABLE `table` ADD COLUMN column2 TEXT;",
                            "UPDATE `table`\n" +
                            "SET\tcolumn2 = 'value2',\n" +
                            "\tcolumn3 = 'value3';"
    );
  }

  @Test
  public void testAddNotNullColumnWithDefaultValue() {
    FieldBundle field = createFieldBundle("column1", "TEXT", null);
    FieldBundle notNullFieldWithDefaultValue = new FieldBundle("", "column2", "TEXT", true, "default");

    testMigrationStatements(createEntityBundle("table", field),
                            createEntityBundle("table", field, notNullFieldWithDefaultValue),
                            "ALTER TABLE `table` ADD COLUMN column2 TEXT DEFAULT 'default' NOT NULL;");
  }

  @Test
  public void testAddNotNullColumnWithoutDefaultValue() {
    FieldBundle idField = new FieldBundle("id", "id", "INTEGER", true, null);
    FieldBundle nameField = new FieldBundle("name", "name", "TEXT", true, null);

    EntityBundle idOnly = createEntityBundle("my_table", idField);
    EntityBundle idAndName = createEntityBundle("my_table", idField, nameField);

    EntityUpdate entityUpdate = new EntityUpdate(idOnly, idAndName);
    entityUpdate.setValuesForUninitializedFields(ImmutableMap.of(nameField, "John Doe"));

    testMigrationStatements(entityUpdate,
                            "CREATE TABLE my_table_data$android_studio_tmp\n" +
                            "(\n" +
                            "\tid INTEGER NOT NULL,\n" +
                            "\tname TEXT NOT NULL,\n" +
                            "\tPRIMARY KEY (id)\n" +
                            ");",
                            "INSERT INTO my_table_data$android_studio_tmp (id, name)\n" +
                            "\tSELECT id, `'John Doe'`\n" +
                            "\tFROM my_table;",
                            "DROP TABLE my_table;",
                            "ALTER TABLE my_table_data$android_studio_tmp RENAME TO my_table;");
  }

  @Test
  public void testRenameColumn() {
    FieldBundle field = createFieldBundle("column1", "TEXT", null);
    FieldBundle fieldToRename = createFieldBundle("column2", "TEXT", null);
    FieldBundle renamedField = createFieldBundle("column3", "TEXT", null);

    EntityBundle entity = createEntityBundle("table", field, fieldToRename);
    EntityBundle entityWithRenamedField = createEntityBundle("table", field, renamedField);

    EntityUpdate entityUpdate = new EntityUpdate(entity, entityWithRenamedField);
    entityUpdate.applyRenameMapping(ImmutableMap.of("column2", "column3"));

    testMigrationStatements(entityUpdate,
                            "ALTER TABLE `table` RENAME column2 TO column3;");
  }

  @Test
  public void testComplexUpdateWithRenamedColumnStatements() {
    FieldBundle field = createFieldBundle("column1", "TEXT", null);
    FieldBundle fieldToRename = createFieldBundle("column2", "TEXT", null);
    FieldBundle renamedField = createFieldBundle("column3", "TEXT", null);
    FieldBundle deletedField = createFieldBundle("column");

    EntityBundle entity = createEntityBundle("table", field, fieldToRename, deletedField);
    EntityBundle modifiedEntity = createEntityBundle("table", field, renamedField);

    EntityUpdate entityUpdate = new EntityUpdate(entity, modifiedEntity);
    entityUpdate.applyRenameMapping(ImmutableMap.of("column2", "column3"));

    testMigrationStatements(entityUpdate,
                            "CREATE TABLE table_data$android_studio_tmp\n" +
                            "(\n" +
                            "\tcolumn1 TEXT,\n" +
                            "\tcolumn3 TEXT,\n" +
                            "\tPRIMARY KEY (column1)\n" +
                            ");",
                            "INSERT INTO table_data$android_studio_tmp (column1, column3)\n" +
                            "\tSELECT column1, column2\n" +
                            "\tFROM `table`;",
                            "DROP TABLE `table`;",
                            "ALTER TABLE table_data$android_studio_tmp RENAME TO `table`;");
  }

  @Test
  public void testRenameAndModifyColumnStatements() {
    FieldBundle field = createFieldBundle("column2", "TEXT", null);
    FieldBundle fieldToModifyAndRename = createFieldBundle("column3", "TEXT", null);
    FieldBundle renamedAndModifiedField = createFieldBundle("column1", "CHAR", null);

    EntityBundle entity1 = createEntityBundle("table", field, fieldToModifyAndRename);
    EntityBundle entity2 = createEntityBundle("table", field, renamedAndModifiedField);

    EntityUpdate entityUpdate = new EntityUpdate(entity1, entity2);
    entityUpdate.applyRenameMapping(ImmutableMap.of("column3", "column1"));

    testMigrationStatements(entityUpdate,
                            "CREATE TABLE table_data$android_studio_tmp\n" +
                            "(\n" +
                            "\tcolumn2 TEXT,\n" +
                            "\tcolumn1 CHAR,\n" +
                            "\tPRIMARY KEY (column2)\n" +
                            ");",
                            "INSERT INTO table_data$android_studio_tmp (column2, column1)\n" +
                            "\tSELECT column2, column3\n" +
                            "\tFROM `table`;",
                            "DROP TABLE `table`;",
                            "ALTER TABLE table_data$android_studio_tmp RENAME TO `table`;");
  }

  @Test
  public void testAddEntity() {
    FieldBundle field1 = createFieldBundle("column1", "TEXT", null);
    FieldBundle field2 = createFieldBundle("column2", "TEXT", null);
    FieldBundle field3 = createFieldBundle("column3", "TEXT", null);

    EntityBundle entity1 = createEntityBundle("table1", field1, field2);
    EntityBundle entity2 = createEntityBundle("table2", field1, field2, field3);
    EntityBundle entityToAdd = createEntityBundle("table3", field1, field3);

    testMigrationStatements(createDatabaseBundle(1, entity1, entity2),
                            createDatabaseBundle(2, entity1, entity2, entityToAdd),
                            "CREATE TABLE table3\n" +
                            "(\n" +
                            "\tcolumn1 TEXT,\n" +
                            "\tcolumn3 TEXT,\n" +
                            "\tPRIMARY KEY (column1)\n" +
                            ");");
  }

  @Test
  public void testDeleteEntity() {
    FieldBundle field1 = createFieldBundle("column1", "TEXT", null);
    FieldBundle field2 = createFieldBundle("column2", "TEXT", null);
    FieldBundle field3 = createFieldBundle("column3", "TEXT", null);

    EntityBundle entity1 = createEntityBundle("table1", field1, field2);
    EntityBundle entity2 = createEntityBundle("table2", field1, field2, field3);
    EntityBundle entity3 = createEntityBundle("table3", field1, field3);

    testMigrationStatements(createDatabaseBundle(1, entity1, entity2, entity3),
                            createDatabaseBundle(2, entity1, entity3),
                            "DROP TABLE table2;");
  }

  @Test
  public void testModifyEntity() {
    FieldBundle fieldToModify = createFieldBundle("column1", "TEXT", null);
    FieldBundle modifiedField = createFieldBundle("column1", "CHAR", null);
    FieldBundle field = createFieldBundle("column2", "TEXT", null);

    EntityBundle entityToModify = createEntityBundle("table1", fieldToModify, field);
    EntityBundle modifiedEntity = createEntityBundle("table1", modifiedField, field);
    EntityBundle unmodifiedEntity = createEntityBundle("table2", field);

    testMigrationStatements(createDatabaseBundle(1, entityToModify, unmodifiedEntity),
                            createDatabaseBundle(2, modifiedEntity, unmodifiedEntity),
                            "CREATE TABLE table1_data$android_studio_tmp\n" +
                            "(\n" +
                            "\tcolumn1 CHAR,\n" +
                            "\tcolumn2 TEXT,\n" +
                            "\tPRIMARY KEY (column1)\n" +
                            ");",
                            "INSERT INTO table1_data$android_studio_tmp (column1, column2)\n" +
                            "\tSELECT column1, column2\n" +
                            "\tFROM table1;",
                            "DROP TABLE table1;",
                            "ALTER TABLE table1_data$android_studio_tmp RENAME TO table1;");
  }

  @Test
  public void testModifyPrimaryKey() {
    FieldBundle field1 = createFieldBundle("column1", "TEXT", null);
    FieldBundle field2 = createFieldBundle("column2", "TEXT", null);
    FieldBundle field3 = createFieldBundle("column3", "TEXT", null);

    EntityBundle entity = createEntityBundle("table", field1, field2, field3);
    EntityBundle entityWithChangesPrimaryKey = new EntityBundle("table",
                                                                "",
                                                                Arrays.asList(field1, field2, field3),
                                                                new PrimaryKeyBundle(false, Arrays
                                                                  .asList(field1.getColumnName(), field2.getColumnName())),
                                                                Collections.emptyList(),
                                                                Collections.emptyList());

    testMigrationStatements(entity,
                            entityWithChangesPrimaryKey,
                            "CREATE TABLE table_data$android_studio_tmp\n" +
                            "(\n" +
                            "\tcolumn1 TEXT,\n" +
                            "\tcolumn2 TEXT,\n" +
                            "\tcolumn3 TEXT,\n" +
                            "\tPRIMARY KEY (column1, column2)\n" +
                            ");",
                            "INSERT INTO table_data$android_studio_tmp (column1, column2, column3)\n" +
                            "\tSELECT column1, column2, column3\n" +
                            "\tFROM `table`;",
                            "DROP TABLE `table`;",
                            "ALTER TABLE table_data$android_studio_tmp RENAME TO `table`;");
  }

  @Test
  public void testPrimaryKeyAutoIncrement() {
    FieldBundle field1 = createFieldBundle("column1", "TEXT", null);
    FieldBundle field2 = createFieldBundle("column2", "TEXT", null);
    FieldBundle field3 = createFieldBundle("column3", "TEXT", null);

    FieldBundle autoIncrementField = createFieldBundle("column1", "INTEGER", null);
    EntityBundle entity = createEntityBundle("table", autoIncrementField, field2, field3);
    EntityBundle entityWithAutoIncrementPrimaryKey = new EntityBundle("table",
                                                                      "",
                                                                      Arrays.asList(autoIncrementField, field2, field3),
                                                                      new PrimaryKeyBundle(true, Collections
                                                                        .singletonList(field1.getColumnName())),
                                                                      Collections.emptyList(),
                                                                      Collections.emptyList());

    testMigrationStatements(entity,
                            entityWithAutoIncrementPrimaryKey,
                            "CREATE TABLE table_data$android_studio_tmp\n" +
                            "(\n" +
                            "\tcolumn1 INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                            "\tcolumn2 TEXT,\n" +
                            "\tcolumn3 TEXT\n" +
                            ");",
                            "INSERT INTO table_data$android_studio_tmp (column1, column2, column3)\n" +
                            "\tSELECT column1, column2, column3\n" +
                            "\tFROM `table`;",
                            "DROP TABLE `table`;",
                            "ALTER TABLE table_data$android_studio_tmp RENAME TO `table`;");
  }

  @Test
  public void testAddForeignKey() {
    FieldBundle field1 = createFieldBundle("column1", "TEXT", null);
    FieldBundle field2 = createFieldBundle("column2", "TEXT", null);
    FieldBundle field3 = createFieldBundle("column3", "TEXT", null);

    List<ForeignKeyBundle> foreignKeys =
      Collections.singletonList(new ForeignKeyBundle("table1", "", "", Collections.singletonList(field1.getColumnName()),
                                                     Collections.singletonList(field1.getColumnName())));
    EntityBundle referencedEntity = createEntityBundle("table1", field1, field2);
    EntityBundle entity = createEntityBundle("table2", field1, field2, field3);
    EntityBundle entityWithAddedForeignKeyConstraints = new EntityBundle("table2",
                                                                         "",
                                                                         Arrays.asList(field1, field2, field3),
                                                                         new PrimaryKeyBundle(false, Collections
                                                                           .singletonList(field1.getColumnName())),
                                                                         Collections.emptyList(),
                                                                         foreignKeys);

    testMigrationStatements(createDatabaseBundle(1, referencedEntity, entity),
                            createDatabaseBundle(2, referencedEntity, entityWithAddedForeignKeyConstraints),
                            "CREATE TABLE table2_data$android_studio_tmp\n" +
                            "(\n" +
                            "\tcolumn1 TEXT,\n" +
                            "\tcolumn2 TEXT,\n" +
                            "\tcolumn3 TEXT,\n" +
                            "\tPRIMARY KEY (column1),\n" +
                            "\tFOREIGN KEY (column1) REFERENCES table1 (column1)\n" +
                            ");",
                            "INSERT INTO table2_data$android_studio_tmp (column1, column2, column3)\n" +
                            "\tSELECT column1, column2, column3\n" +
                            "\tFROM table2;",
                            "DROP TABLE table2;",
                            "ALTER TABLE table2_data$android_studio_tmp RENAME TO table2;",
                            "PRAGMA foreign_key_check;");
  }

  @Test
  public void testDropIndex() {
    FieldBundle field1 = createFieldBundle("column1", "TEXT", null);
    FieldBundle field2 = createFieldBundle("column2", "TEXT", null);

    IndexBundle indexBundle = new IndexBundle("index_table_column1", false, Collections.singletonList("column1"), "");

    EntityBundle entityWithIndex = new EntityBundle("table", "", Arrays.asList(field1, field2),
                                                    new PrimaryKeyBundle(false, Collections.singletonList(field1.getColumnName())),
                                                    Collections.singletonList(indexBundle), Collections.emptyList());
    EntityBundle entityAfterIndexDrop = createEntityBundle("table", field1, field2);

    testMigrationStatements(entityWithIndex, entityAfterIndexDrop, "DROP INDEX index_table_column1;");
  }

  @Test
  public void testAddIndex() {
    FieldBundle field1 = createFieldBundle("column1", "TEXT", null);
    FieldBundle field2 = createFieldBundle("column2", "TEXT", null);

    IndexBundle indexBundle = new IndexBundle("index_table_column1", false, Collections.singletonList("column1"), "");

    EntityBundle entity = createEntityBundle("table", field1, field2);
    EntityBundle entityWithIndex = new EntityBundle("table", "", Arrays.asList(field1, field2),
                                                    new PrimaryKeyBundle(false, Collections.singletonList(field1.getColumnName())),
                                                    Collections.singletonList(indexBundle), Collections.emptyList());

    testMigrationStatements(entity, entityWithIndex, "CREATE INDEX index_table_column1 ON `table` (column1);");
  }

  @Test
  public void testAddUniqueIndex() {
    FieldBundle field1 = createFieldBundle("column1", "TEXT", null);
    FieldBundle field2 = createFieldBundle("column2", "TEXT", null);

    IndexBundle indexBundle = new IndexBundle("index_table_column1", true, Collections.singletonList("column1"), "");

    EntityBundle entity = createEntityBundle("table", field1, field2);
    EntityBundle entityWithUniqueIndex = new EntityBundle("table", "", Arrays.asList(field1, field2),
                                                          new PrimaryKeyBundle(false, Collections.singletonList(field1.getColumnName())),
                                                          Collections.singletonList(indexBundle), Collections.emptyList());

    testMigrationStatements(entity, entityWithUniqueIndex, "CREATE UNIQUE INDEX index_table_column1 ON `table` (column1);");
  }

  @Test
  public void testRenameIndex() {
    FieldBundle field1 = createFieldBundle("column1", "TEXT", null);
    FieldBundle field2 = createFieldBundle("column2", "TEXT", null);

    IndexBundle index = new IndexBundle("index_table_column1", false, Collections.singletonList("column1"), "");
    IndexBundle indexRenamedIndex = new IndexBundle("index_column1", false, Collections.singletonList("column1"), "");

    EntityBundle entity = new EntityBundle("table", "", Arrays.asList(field1, field2),
                                           new PrimaryKeyBundle(false, Collections.singletonList(field1.getColumnName())),
                                           Collections.singletonList(index), Collections.emptyList());
    EntityBundle entityWithRenamedIndex = new EntityBundle("table", "", Arrays.asList(field1, field2),
                                                           new PrimaryKeyBundle(false, Collections.singletonList(field1.getColumnName())),
                                                           Collections.singletonList(indexRenamedIndex), Collections.emptyList());

    testMigrationStatements(entity, entityWithRenamedIndex, "DROP INDEX index_table_column1;",
                            "CREATE INDEX index_column1 ON `table` (column1);");
  }

  @Test
  public void testUpdateIndexOnTableChange() {
    FieldBundle field1 = createFieldBundle("column1", "TEXT", null);
    FieldBundle field2 = createFieldBundle("column2", "TEXT", null);
    FieldBundle field3 = createFieldBundle("column3", "TEXT", null);

    IndexBundle index = new IndexBundle("index_table_column1", false, Collections.singletonList("column1"), "");

    EntityBundle entity = new EntityBundle("table", "", Arrays.asList(field1, field2, field3),
                                           new PrimaryKeyBundle(false, Collections.singletonList(field1.getColumnName())),
                                           Collections.singletonList(index), Collections.emptyList());
    EntityBundle entityUpdatedEntity = new EntityBundle("table", "", Arrays.asList(field1, field2),
                                                        new PrimaryKeyBundle(false, Collections.singletonList(field1.getColumnName())),
                                                        Collections.singletonList(index), Collections.emptyList());
    testMigrationStatements(entity,
                            entityUpdatedEntity,
                            "CREATE TABLE table_data$android_studio_tmp\n" +
                            "(\n" +
                            "\tcolumn1 TEXT,\n" +
                            "\tcolumn2 TEXT,\n" +
                            "\tPRIMARY KEY (column1)\n" +
                            ");",
                            "INSERT INTO table_data$android_studio_tmp (column1, column2)\n" +
                            "\tSELECT column1, column2\n" +
                            "\tFROM `table`;",
                            "DROP TABLE `table`;",
                            "ALTER TABLE table_data$android_studio_tmp RENAME TO `table`;",
                            "CREATE INDEX index_table_column1 ON `table` (column1);");
  }

  @Test
  public void dropView() {
    DatabaseViewBundle view = new DatabaseViewBundle(
      "viewName",
      "CREATE VIEW ${VIEW_NAME} as\n" +
      "SELECT column1, column2, column3\n" +
      "FROM myTable;");

    testMigrationStatements(new DatabaseBundle(1, "", Collections.emptyList(), Collections.singletonList(view), Collections.emptyList()),
                            new DatabaseBundle(2, "", Collections.emptyList(), Collections.emptyList(), Collections.emptyList()),
                            "DROP VIEW viewName;");
  }

  @Test
  public void addView() {
    DatabaseViewBundle view = new DatabaseViewBundle(
      "viewName",
      "CREATE VIEW ${VIEW_NAME} as\n" +
      "SELECT column1, column2, column3\n" +
      "FROM myTable;");

    testMigrationStatements(new DatabaseBundle(1, "", Collections.emptyList(), Collections.emptyList(), Collections.emptyList()),
                            new DatabaseBundle(2, "", Collections.emptyList(), Collections.singletonList(view), Collections.emptyList()),
                            "CREATE VIEW viewName as\n" +
                            "SELECT column1, column2, column3\n" +
                            "FROM myTable;");
  }

  @Test
  public void modifyView() {
    DatabaseViewBundle view1 = new DatabaseViewBundle(
      "viewName",
      "CREATE VIEW ${VIEW_NAME} as\n" +
      "SELECT column1, column2, column3\n" +
      "FROM myTable;");
    DatabaseViewBundle view2 = new DatabaseViewBundle(
      "viewName",
      "CREATE VIEW ${VIEW_NAME} as\n" +
      "SELECT column1, column2\n" +
      "FROM myTable;");

    testMigrationStatements(new DatabaseBundle(1, "", Collections.emptyList(), Collections.singletonList(view1), Collections.emptyList()),
                            new DatabaseBundle(2, "", Collections.emptyList(), Collections.singletonList(view2), Collections.emptyList()),
                            "DROP VIEW viewName;",
                            "CREATE VIEW viewName as\n" +
                            "SELECT column1, column2\n" +
                            "FROM myTable;");
  }

  @Test
  public void addFtsTable() {
    FieldBundle field1 = createFieldBundle("column1", "TEXT", null);
    FieldBundle field2 = createFieldBundle("column2", "TEXT", null);

    EntityBundle entity = createEntityBundle("table", field1, field2);
    FtsEntityBundle ftsEntity = createFtsEntityBundle("ftsTable",
                                                      "CREATE VIRTUAL TABLE ${TABLE_NAME} USING fts4(column1, column2)",
                                                      field1, field2);

    testMigrationStatements(createDatabaseBundle(1, entity),
                            createDatabaseBundle(2, entity, ftsEntity),
                            "CREATE VIRTUAL TABLE ftsTable USING fts4(column1, column2);");
  }

  @Test
  public void dropFtsTable() {
    FieldBundle field1 = createFieldBundle("column1", "TEXT", null);
    FieldBundle field2 = createFieldBundle("column2", "TEXT", null);

    EntityBundle entity = createEntityBundle("table", field1, field2);
    FtsEntityBundle ftsEntity = createFtsEntityBundle("ftsTable",
                                                      "CREATE VIRTUAL TABLE ${TABLE_NAME} USING fts4(column1, column2)",
                                                      field1, field2);

    testMigrationStatements(createDatabaseBundle(2, entity, ftsEntity),
                            createDatabaseBundle(1, entity),
                            "DROP TABLE ftsTable;");
  }

  @Test
  public void modifyFtsTable() {
    FieldBundle field1 = createFieldBundle("column1", "TEXT", null);
    FieldBundle field2 = createFieldBundle("column2", "TEXT", null);

    FtsEntityBundle ftsEntity = createFtsEntityBundle("ftsTable",
                                                      "CREATE VIRTUAL TABLE ${TABLE_NAME} USING fts4(column1)",
                                                      field1);
    FtsEntityBundle modifiedFtsEntity = createFtsEntityBundle("ftsTable",
                                                              "CREATE VIRTUAL TABLE ${TABLE_NAME} USING fts4(column1, column2)",
                                                              field1, field2);

    testMigrationStatements(createDatabaseBundle(1, ftsEntity),
                            createDatabaseBundle(2, modifiedFtsEntity),
                            "CREATE VIRTUAL TABLE ftsTable_data$android_studio_tmp USING fts4(column1, column2);",
                            "INSERT INTO ftsTable_data$android_studio_tmp (column1)\n" +
                            "\tSELECT column1\n" +
                            "\tFROM ftsTable;",
                            "DROP TABLE ftsTable;",
                            "ALTER TABLE ftsTable_data$android_studio_tmp RENAME TO ftsTable;");
  }

  @Test
  public void updateFtsTableWithContentTable() {
    FieldBundle field1 = createFieldBundle("column1", "TEXT", null);
    FieldBundle field2 = createFieldBundle("column2", "TEXT", null);

    EntityBundle entity = createEntityBundle("table", field1, field2);
    FtsEntityBundle ftsEntity = createFtsEntityBundle("ftsTable",
                                                      "CREATE VIRTUAL TABLE ${TABLE_NAME} USING fts4(column1, column2)",
                                                      field1, field2);
    FtsEntityBundle ftsEntityWithContentTable = createFtsEntityBundle("ftsTable",
                                                                      "CREATE VIRTUAL TABLE ${TABLE_NAME} USING fts4(column1, column2)",
                                                                      "table", field1, field2);

    testMigrationStatements(createDatabaseBundle(1, entity, ftsEntity),
                            createDatabaseBundle(2, entity, ftsEntityWithContentTable),
                            "DROP TABLE ftsTable;",
                            "CREATE VIRTUAL TABLE ftsTable USING fts4(column1, column2);",
                            "INSERT INTO ftsTable (column1, column2)\n" +
                            "\tSELECT column1, column2\n" +
                            "\tFROM `table`;");
  }
}

------------------ MSSQL ------------------
CREATE PROCEDURE ModifyMyPK
    AS
BEGIN
    DECLARE @sql NVARCHAR(MAX);
    DECLARE @indexName NVARCHAR(128);
    DECLARE @tableName NVARCHAR(128);

    -- 声明游标，获取需要修改的表
    DECLARE cur CURSOR FOR
SELECT TABLE_NAME
FROM INFORMATION_SCHEMA.COLUMNS
WHERE COLUMN_NAME = 'MyPK'
  AND TABLE_NAME LIKE 'nd%track';

OPEN cur;

FETCH NEXT FROM cur INTO @tableName;

WHILE @@FETCH_STATUS = 0
BEGIN
        -- 找到依赖的索引
        DECLARE index_cur CURSOR FOR
SELECT i.name
FROM sys.indexes i
         JOIN sys.index_columns ic ON i.index_id = ic.index_id AND i.object_id = ic.object_id
         JOIN sys.columns c ON ic.column_id = c.column_id AND ic.object_id = c.object_id
WHERE c.name = 'MyPK'
  AND OBJECT_NAME(i.object_id) = @tableName;

OPEN index_cur;

FETCH NEXT FROM index_cur INTO @indexName;

-- 删除依赖该列的索引
WHILE @@FETCH_STATUS = 0
BEGIN
            SET @sql = 'DROP INDEX ' + QUOTENAME(@indexName) + ' ON ' + QUOTENAME(@tableName);
EXEC sp_executesql @sql;

FETCH NEXT FROM index_cur INTO @indexName;
END

CLOSE index_cur;
DEALLOCATE index_cur;

        -- 修改列类型为BIGINT
        SET @sql = 'ALTER TABLE ' + QUOTENAME(@tableName) + ' ALTER COLUMN MyPK BIGINT';
EXEC sp_executesql @sql;

        -- 重新创建索引（根据业务需求，你可能需要手动指定索引的具体定义）
        -- 这里假设索引是单列的，如果是复合索引，需要调整索引创建语句
        SET @sql = 'CREATE INDEX ' + QUOTENAME(@indexName) + ' ON ' + QUOTENAME(@tableName) + ' (MyPK)';
EXEC sp_executesql @sql;

FETCH NEXT FROM cur INTO @tableName;
END

CLOSE cur;
DEALLOCATE cur;
END;
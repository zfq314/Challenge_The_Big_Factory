##### mysql索引

```sql
msyql 8.x:
			1. 默认字符集由latin1变为utf8mb4
			2. MyISAM系统表全部换成InnoDB表
			3. 自增变量持久化
			4. DDL原子化
			5. 参数修改持久化
			6. 新增降序索引
			7. group by 不再隐式排序
			8. JSON特性增强
			9. redo & undo 日志加密
			10. innodb select for update跳过锁等待
			11. 增加SET_VAR语法
			12. 支持不可见索引
			13. 支持直方图
			14. 新增innodb_dedicated_server参数
			15. 日志分类更详细
			16. undo空间自动回收
			17. 增加资源组
			18. 增加角色管理
			19. 支持窗口函数
			开窗的应用
			       
                   select 
                field 
        from 
           (SELECT CONCAT(column_name,',') AS field,ORDINAL_POSITION as a1 ,lead(ORDINAL_POSITION,1) over(ORDER BY ORDINAL_POSITION asc) as a2  FROM information_schema.COLUMNS WHERE  table_schema='decent_cloud' and table_name = "t_fast_package" ORDER BY ORDINAL_POSITION asc
                 ) m 
                 where m.a2 is not null 
        union all
        select 
                field 
        from 
           (SELECT column_name AS field,ORDINAL_POSITION as a1 ,lead(ORDINAL_POSITION,1) over(ORDER BY ORDINAL_POSITION asc) as a2  FROM information_schema.COLUMNS WHERE  table_schema='decent_cloud' and table_name = "t_fast_package" ORDER BY ORDINAL_POSITION asc
                        ) m 
                        where m.a2 is  null
                        
```

##### sqlserver版本获取

```
 select @@version
```


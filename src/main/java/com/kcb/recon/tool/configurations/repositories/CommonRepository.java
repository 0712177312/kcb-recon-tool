package com.kcb.recon.tool.configurations.repositories;

import com.kcb.recon.tool.authentication.utils.AppUtillities;
import com.kcb.recon.tool.configurations.entities.Subsidiary;
import com.kcb.recon.tool.configurations.extras.Menu1;
import com.kcb.recon.tool.configurations.extras.MenuPermission1;
import com.kcb.recon.tool.configurations.extras.SubMenu1;
import com.kcb.recon.tool.configurations.extras.SubMenuPermission1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
@Slf4j
public class CommonRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public CommonRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Subsidiary findByCompanyName(String companyCode) {
        log.info("FindByCode {}", companyCode);
        String sql = "SELECT * FROM CONFIG_COMPANY_CODE WHERE COMPANY_CODE = :name";
        Map<String, Object> params = new HashMap<>();
        params.put("name", companyCode);

        Subsidiary subsidiary = null;
        try {
            subsidiary = namedParameterJdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Subsidiary.class));
        } catch (EmptyResultDataAccessException e) {
            String logMessage = AppUtillities.logPreString() + AppUtillities.ERROR + e.getMessage() + AppUtillities.STACKTRACE + AppUtillities.getExceptionStacktrace(e);
            log.error("log-message -> {}", logMessage);
            log.warn("No subsidiary found for company code {}", companyCode);
        }

        log.info("Result: {}", subsidiary);
        return subsidiary;
    }


    public Subsidiary findByCompanyCode(String companyCode) {
        log.info("FindByCode {}", companyCode);
        String sql = "SELECT * FROM CONFIG_COMPANY_CODE WHERE COMPANY_CODE = :name";
        Map<String, Object> params = new HashMap<>();
        params.put("name", companyCode);

        Subsidiary subsidiary = null;
        try {
            subsidiary = namedParameterJdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Subsidiary.class));
        } catch (EmptyResultDataAccessException e) {
            String logMessage = AppUtillities.logPreString() + AppUtillities.ERROR + e.getMessage() + AppUtillities.STACKTRACE + AppUtillities.getExceptionStacktrace(e);
            log.error("log-message -> {}", logMessage);
            log.warn("No subsidiary found for company code {}", companyCode);
        }

        log.info("Result: {}", subsidiary);
        return subsidiary;
    }

    public List<Subsidiary> allWithoutPagination() {
        log.info("Fetching all active countries");
        String sql = "SELECT * FROM CONFIG_COMPANY_CODE WHERE status = :status";

        Map<String, Object> params = new HashMap<>();
        params.put("status", "ACTIVE");

        List<Subsidiary> subsidiaries = namedParameterJdbcTemplate.query(
                sql,
                params,
                new BeanPropertyRowMapper<>(Subsidiary.class)
        );
        log.info("Fetched {} subsidiaries", subsidiaries.size());
        return subsidiaries;
    }

    public Optional<Subsidiary> findByName(String name) {
        log.info("Searching for country with name: {}", name);

        String sql = "SELECT * FROM CONFIG_COMPANY_CODE WHERE TRIM(COMPANY_NAME) = TRIM(:name)";
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        try {
            Subsidiary subsidiary = namedParameterJdbcTemplate.queryForObject(
                    sql,
                    params,
                    new BeanPropertyRowMapper<>(Subsidiary.class)
            );
            return Optional.ofNullable(subsidiary);
        } catch (EmptyResultDataAccessException e) {
            log.warn("No country found for name: {}", name);
            return Optional.empty();
        }
    }

    public Integer findIdByMenuName(String name) {
        String sql = "SELECT id FROM menu1 WHERE name = :name";
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    public Integer findIdBySubMenuName(String name) {
        String sql = "SELECT id FROM sub_menu1 WHERE name = :name";
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    public void insertMenu(Integer menuId, String menuName) {
        String sql = "INSERT INTO MENU1_ALLOWED (MENU1_ID, ALLOWED) VALUES (:menuId, :menuName)";
        Map<String, Object> params = new HashMap<>();
        params.put("menuId", menuId);
        params.put("menuName", menuName);
        namedParameterJdbcTemplate.update(sql, params);
    }

    public void insertSubMenu(Integer submenuId, String roleName) {
        log.info("inside insert sub menu | {},{}",submenuId,roleName);
        String sql = "INSERT INTO SUB_MENU1_ALLOWED (SUB_MENU1_ID, ALLOWED) VALUES (:submenuId, :roleName)";
        Map<String, Object> params = new HashMap<>();
        params.put("submenuId", submenuId);
        params.put("roleName", roleName);
        namedParameterJdbcTemplate.update(sql, params);
    }

    public List<Long> findSubMenuIdsByRoleName(String roleName) {
        String sql = "SELECT SUB_MENU1_ID FROM SUB_MENU1_ALLOWED WHERE ALLOWED = :roleName";
        Map<String, Object> params = Map.of("roleName", roleName);

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> rs.getLong("SUB_MENU1_ID"));
    }

    public List<Long> findMenuIdsByRoleName(String roleName) {
        String sql = "SELECT MENU1_ID FROM MENU1_ALLOWED WHERE ALLOWED = :roleName";
        Map<String, Object> params = Map.of("roleName", roleName);

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> rs.getLong("MENU1_ID"));
    }

    public List<Menu1> findByMenuIdIn(List<Long> menuIds) {
        String sql = "SELECT * FROM menu1 WHERE id IN (:menuIds)";

        Map<String, Object> params = new HashMap<>();
        params.put("menuIds", menuIds);

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            Menu1 menu = new Menu1();
            menu.setId(rs.getLong("id"));
            menu.setName(rs.getString("name"));
            // Map other fields as needed
            return menu;
        });
    }


    public List<SubMenu1> findBySubMenuMenuIdIn(Long menuId, List<Long> subMenuIds) {
        String sql = "SELECT * FROM sub_menu1 WHERE menu_id = :menuId AND id IN (:subMenuIds)";
        Map<String, Object> params = new HashMap<>();
        params.put("subMenuIds", subMenuIds);
        params.put("menuId", menuId);
        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            SubMenu1 submenu = new SubMenu1();
            submenu.setId(rs.getLong("id"));
            submenu.setName(rs.getString("name"));
            submenu.setType(rs.getString("type"));
            submenu.setRoute(rs.getString("route"));
            submenu.setIcon(rs.getString("icon"));
            return submenu;
        });
    }

    public List<SubMenu1> findBySubMenuIdIn(List<String> subMenuIds) {
        String sql = "SELECT * FROM sub_menu1 WHERE id IN (:subMenuIds)";

        Map<String, Object> params = new HashMap<>();
        params.put("subMenuIds", subMenuIds);

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            SubMenu1 submenu = new SubMenu1();
            submenu.setId(rs.getLong("id"));
            submenu.setName(rs.getString("name"));
            return submenu;
        });
    }

    public SubMenuPermission1 getSubMenuPermission(Long id) {
        log.info("Searching for permission with id: {}", id);

        String sql = "SELECT * FROM SUB_MENU1_ALLOWED WHERE SUB_MENU1_ID = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        return namedParameterJdbcTemplate.query(sql, params, rs -> {
            SubMenuPermission1 menu = new SubMenuPermission1();
            List<String> allowedList = new ArrayList<>();

            while (rs.next()) {
                String allowed = rs.getString("ALLOWED");
                if (allowed != null && !allowed.isBlank()) {
                    allowedList.add(allowed);
                }
            }

            menu.setAllowed(allowedList);
            return menu;
        });
    }

    public MenuPermission1 getMenuPermission(Long id) {
        String sql = "SELECT * FROM MENU1_ALLOWED WHERE menu1_id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        return namedParameterJdbcTemplate.query(sql, params, rs -> {
            MenuPermission1 menu = new MenuPermission1();
            List<String> allowedList = new ArrayList<>();

            while (rs.next()) {
                String allowed = rs.getString("ALLOWED");
                if (allowed != null && !allowed.isBlank()) {
                    allowedList.add(allowed);
                }
            }

            menu.setAllowed(allowedList);
            return menu;
        });
    }

    public List<SubMenu1> getSubmenus(Long id) {

        String sql = "SELECT * FROM SUB_MENU1 WHERE menu_id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        List<SubMenu1> submenus = namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            SubMenu1 menu = new SubMenu1();
            menu.setId(rs.getLong("id"));
            menu.setIcon(rs.getString("icon"));
            menu.setBadge(
                    Optional.ofNullable(rs.getString("badge"))
                            .filter(s -> !s.trim().isEmpty())
                            .orElse(null)
            );
            menu.setName(rs.getString("name"));
            menu.setRoute(rs.getString("route"));
            menu.setType(rs.getString("type"));
            return menu;
        });

        return submenus != null ? submenus : Collections.emptyList();
    }

    public void updateUserRoles(Long roleId, Long userId) {

        String sql = "UPDATE USER_ROLES SET ROLE_ID =:roleId WHERE USER_ID =:userId";
        Map<String, Object> params = new HashMap<>();
        params.put("roleId", roleId);
        params.put("userId", userId);
        namedParameterJdbcTemplate.update(sql, params);
    }

    public boolean getAllWithRoleName(String name) {
        String sql = "SELECT COUNT(*) FROM SUB_MENU1_ALLOWED WHERE ALLOWED = :name";
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    public boolean deleteFromSubMenu(String name) {
        String sql = "DELETE FROM SUB_MENU1_ALLOWED WHERE ALLOWED = :name";
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        int count = namedParameterJdbcTemplate.update(sql, params);
        return count > 0;
    }


    public boolean deleteFromMenu(String name) {
        String sql = "DELETE FROM MENU1_ALLOWED WHERE ALLOWED = :name";
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        int count = namedParameterJdbcTemplate.update(sql, params);
        return count > 0;
    }

}

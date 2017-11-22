package com.inks.hb.floorinfo.controller;

import com.google.gson.Gson;
import com.inks.hb.common.PojotoGson;
import com.inks.hb.floorinfo.pojo.FloorInfo;
import com.inks.hb.floorinfo.service.FloorInfoService;
import com.inks.hb.floorinfo.service.FloorInfoServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 与表格相关的全部操作
 * 状态标志： make 0重载 1新增 2修改 3搜索 4删除
 */
@WebServlet(name = "/FloorInfoServlet", value = "/FloorInfoServlet")
public class FloorInfoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();

        int page = Integer.parseInt(request.getParameter("page")); // 当前页码
        int limit = Integer.parseInt(request.getParameter("limit")); // 每页的数据量
        int make = Integer.parseInt(request.getParameter("make"));

        // 调用service
        FloorInfoService service = new FloorInfoServiceImpl();

        // 默认输出信息
        String code = "0"; //状态码
        String msg = "数据查询正常"; //状态信息
        String count = ""; //数据总数
        ArrayList<FloorInfo> list = new ArrayList<>(); //数据内容

        //单个全局属性
        int floorId = 0;
        String floorName = "";
        FloorInfo floorInfo = new FloorInfo();

        try {
            // 状态标志 make 0重载 1新增 2修改 3搜索 4删除

            if (make == 1 || make == 3) {
                floorName = request.getParameter("floorName");
            } else if (make == 2) {
                floorId = Integer.parseInt(request.getParameter("floorId"));
                floorName = request.getParameter("floorName");
                floorInfo = new FloorInfo(floorId, floorName);
            } else if (make == 4) {
                floorId = Integer.parseInt(request.getParameter("floorId"));
            }

            switch (make) {
                case 1:
                    service.insertFloorInfo(floorName);
                    break;
                case 2:
                    service.updateFloorInfo(floorInfo);
                    break;
                case 3:
                    floorInfo = service.query(floorName);
                    list.clear();
                    list.add(floorInfo);
                    break;
                case 4:
                    service.deleteFloorInfo(floorId);
                    break;
            }
            if (make != 3) {
                list = service.query(page, limit);
                count = String.valueOf(service.queryFloorInfoNum());
            } else {
                if (floorInfo.getFloorId() == 0)
                    count = "0";
                else
                    count = "1";
            }
        } catch (SQLException e) {
            code = "1";
            msg = "数据查询出现异常";
            System.out.println(e.getErrorCode());
        } finally {
            PojotoGson pojotoGson = new PojotoGson(code, msg, count, list);
            Gson gson = new Gson();
            out.print(gson.toJson(pojotoGson));
        }
    }
}

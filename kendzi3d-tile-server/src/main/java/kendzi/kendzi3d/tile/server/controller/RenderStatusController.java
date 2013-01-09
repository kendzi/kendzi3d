package kendzi.kendzi3d.tile.server.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import kendzi.kendzi3d.tile.server.dto.RenderStatus;
import kendzi.kendzi3d.tile.server.service.RenderService;
import kendzi.kendzi3d.tile.server.service.RenderStatusService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class RenderStatusController {

    @Autowired
    RenderService renderService;

    @Autowired
    RenderStatusService renderStatusService;


    @RequestMapping(value="/status" )
    public ModelAndView test(HttpServletResponse response) {

        List<RenderStatus> list = this.renderStatusService.findAll();
        ModelAndView modelAndView = new ModelAndView("renderStatus");
        modelAndView.addObject("renderStatusList", list);

        return modelAndView;
    }
}

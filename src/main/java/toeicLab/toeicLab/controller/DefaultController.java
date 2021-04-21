package toeicLab.toeicLab.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import toeicLab.toeicLab.domain.Member;
import toeicLab.toeicLab.user.CurrentUser;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
@RequiredArgsConstructor
class DefaultController implements ErrorController {

    /**
     * 에러발생시 지정된 페이지로 이동합니다.
     * @param request
     * @param response
     * @param member
     * @param model
     * @return modelAndView
     */
    @RequestMapping("/errorPage")
    public ModelAndView handleError(HttpServletRequest request, HttpServletResponse response, @CurrentUser Member member, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        ModelAndView modelAndView = new ModelAndView();

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                modelAndView.setViewName("error/error");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                modelAndView.setViewName("error/error");
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                modelAndView.setViewName("error/error");
            } else {
                modelAndView.setViewName("error/error");
            }
        }
        model.addAttribute("member", member);
        return modelAndView;
    }

    /**
     * Deprecated. since 2.3.0 in favor of setting the property.
     * @return null
     */
    @Override
    public String getErrorPath() {
        return null;
    }
}

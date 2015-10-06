package in.virit;

import com.vaadin.server.*;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;


/* Addons in this ws:
 * 7.5.6
 * OBF
 * switch:org.vaadin.teemu:2.0.2

 */
@WebListener
public class WidgetSet implements javax.servlet.http.HttpSessionListener, 
    java.io.Serializable {
    private boolean inited = false;

    private String wsUrl = "http://cdn.virit.in/ws/vwscdn35ba9292b04c92862c7273f807895e22/vwscdn35ba9292b04c92862c7273f807895e22.nocache.js";
    private String wsName = "vwscdn35ba9292b04c92862c7273f807895e22";
    private boolean wsReady = true;


    public WidgetSet() { 
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        if(!inited) {
            VaadinServletService.getCurrent().addSessionInitListener(event -> {
        VaadinSession.getCurrent().addBootstrapListener(new BootstrapListener() {

            @Override
            public void modifyBootstrapFragment(
                    BootstrapFragmentResponse response) {
            }

            @Override
            public void modifyBootstrapPage(BootstrapPageResponse response) {
            // Update the bootstrap page
                Document document = response.getDocument();
                Element scriptTag = document.getElementsByTag("script").last();
                String script = scriptTag.html();
                scriptTag.html("");
                script = script.replaceAll("\"widgetset\": *\"[^\"]*\"", "\"widgetset\": \"" + wsName + "\"");
                if(!wsUrl.equals("local")) {
                    script = script.replace("});", ",\"widgetsetUrl\":\"" + wsUrl + "\"});");
                }
                if(!wsReady) {
                    script += "\nvar wsname = \"vwscdn35ba9292b04c92862c7273f807895e22\";\nsetTimeout(function() {if(!window[wsname]) {if (window.confirm('The widgetset cloud still hadn\\'t compiled your widgetset. Depending on usage, this may take 0.5 - n minutes. Would you like to try again?')) { window[wsname] = 'foo'; window.location.reload(false);}}}, 14000);";
                }
                scriptTag.appendChild(new DataNode(script, scriptTag.baseUri()));
            }
        });
        inited = true;
            });
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
    }
}

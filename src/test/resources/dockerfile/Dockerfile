FROM tomcat:8
COPY ROOT.war /usr/local/tomcat/webapps/ROOT.war
RUN  cd /usr/local/tomcat/webapps && rm -rf ROOT && ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo Asia/Shanghai > /etc/timezone 
CMD ["catalina.sh", "run"]
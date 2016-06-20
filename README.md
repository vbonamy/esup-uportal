Bienvenue sur esup-uportal, ENT EsupV4
https://www.esup-portail.org/pages/viewpage.action?pageId=257064972

La documentation de référence pour l'installation d'uportal4 s'applique également au package esup-uportal : 
https://wiki.jasig.org/display/UPM42/Installing+uPortal


Notes d'installation :

* prérequis - cf README-JASIG.md

* installation tomcat
  * tar xzf apache-tomcat-7.0.65.tar.gz -C /opt
  * ln -s /opt/apache-tomcat-7.0.65 /opt/tomcat-esup
  * emacs /opt/tomcat-esup/conf/catalina.properties
    * shared.loader=${catalina.base}/shared/classes,${catalina.base}/shared/lib/*.jar
  * emacs /opt/tomcat-esup/conf/context.xml
    * <Context> devient <Context sessionCookiePath="/">
  * rm -rf /opt/tomcat-esup/webapps/*
  * éventuellement, affinage de conf/logging.properties, mise en place de psi-probe (et donc paramétrage de conf/tomcat-users.xml)

* creation base de données postgresql (mysql non recommandé - cf https://wiki.jasig.org/display/UPM42/MySQL )
  * psql
    * create USER esup4 with password 'esup4';
    *  create database esup4;
    * grant all privileges on database esup4 to esup4;
  * vérification pg_hba.conf : 
    * host    all         all         127.0.0.1/8     password

* Apache en virtualhost
  * ProxyPass / ajp://tomcat.univ-ville.fr:8009/

* récupération esup-uportal 
  * git clone https://github.com/EsupPortail/esup-uportal.git
  * branche esup-4.2 : git checkout -b esup-4.2 origin/esup-4.2
  * récupération du patch pour postgresql et pb de caractères accentués (issue UP-3488)
    - git merge origin/UP-3488

* build.properties
  * ln -s build.properties.sample build.properties
  * emacs build.properties
    * server.home=/opt/tomcat-esup

* filters/esup.properties
  * rép. de travail : 
    * environment.build.log.dir=/opt/tomcat-esup/logs
  * virtualhost 
    * environment.build.uportal.server=ent.univ-ville.fr
    * environment.build.real.uportal.server=ent.univ-ville.fr
  * base de données 
    * environment.build.hibernate.connection.driver_class=org.postgresql.Driver
    * environment.build.hibernate.connection.url=jdbc:postgresql://pg.mon-univ.fr/esup4
    * environment.build.hibernate.connection.username=esup4
    * environment.build.hibernate.connection.password=esup4
    * environment.build.jdbc.groupId=postgresql
    * environment.build.jdbc.artifactId=postgresql
    * environment.build.jdbc.version=9.0-801.jdbc4
  * cas
    * environment.build.cas.server=cas.univ-ville.fr
    * environment.build.cas.protocol=https
    * environment.build.cas.context=
  * ldap
    * environment.build.ldap.url=ldap://ldap.univ-ville.fr
    * environment.build.ldap.baseDn=dc=univ-ville,dc=fr
    * environment.build.ldap.userName=
    * environment.build.ldap.password=
    * environment.build.ldap.pooled=false
    * environment.build.ldap.uidAttr=uid

* ajout dépendance Postgresql
  * emacs uportal-db/pom.xml
    * on décommente le bloc relatif à la dépendance sur postgresql

* déploiement
  * la première fois : 
    * ant -Dmaven.test.skip=true clean initportal 
  * puis (pour prise en compte modifs configs) :
    * ant -Dmaven.test.skip=true clean deploy-ear

* lancement
  * cd /opt/tomcat-esup/bin ; ./startup.sh ; tail -f /opt/tomcat-esup/logs/*
  * firefox http://ent.univ-ville.fr/uPortal/

* ajout d'un administrateur en ligne de commandes (celui-ci aura alors accès à l'IHM d'administration)
[https://wiki.jasig.org/display/UPM42/Add+Portal+Admininstrator]
  * ant -Dmaven.test.skip=true data-export -Dtype=group-membership -Dsysid="Portal Administrators" -Ddir=/tmp
  * emacs /tmp/Portal_Administrators.group-membership.xml
  * ant -Dmaven.test.skip=true data-import -Dfile=/tmp/Portal_Administrators.group-membership.xml

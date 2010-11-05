/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portal.persondir.dao.jpa;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.jasig.portal.persondir.ILocalAccountDao;
import org.jasig.portal.persondir.ILocalAccountPerson;
import org.jasig.portal.persondir.LocalAccountQuery;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("localAccountDao")
public class JpaLocalAccountDaoImpl implements ILocalAccountDao {

    private static final String FIND_ALL_ACCOUNTS = "from LocalAccountPersonImpl account";
    private static final String FIND_ACCOUNT_BY_NAME = 
        "from LocalAccountPersonImpl account where account.name = :name";

    private static final String FIND_ALL_ACCOUNTS_CACHE_REGION = LocalAccountPersonImpl.class.getName() + ".query.FIND_ALL_ACCOUNTS";
    private static final String FIND_ACCOUNT_BY_NAME_CACHE_REGION = LocalAccountPersonImpl.class.getName() + ".query.FIND_ACCOUNT_BY_NAME";

    private EntityManager entityManager;
    
    /**
     * @param entityManager the entityManager to set
     */
    @PersistenceContext(unitName="uPortalPersistence")
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public ILocalAccountPerson getPerson(long id) {
        return entityManager.find(LocalAccountPersonImpl.class, id);
    }
    
    public ILocalAccountPerson getPerson(String username) {
        final Query query = this.entityManager.createQuery(FIND_ACCOUNT_BY_NAME);
        query.setParameter("name", username);
        query.setHint("org.hibernate.cacheable", true);
        query.setHint("org.hibernate.cacheRegion", FIND_ACCOUNT_BY_NAME_CACHE_REGION);
        query.setMaxResults(1);
        
        @SuppressWarnings("unchecked")
        final List<ILocalAccountPerson> accounts = query.getResultList();
        ILocalAccountPerson account = (ILocalAccountPerson) DataAccessUtils.uniqueResult(accounts);
        return account;
    }

    public List<ILocalAccountPerson> getAllAccounts() {
        final Query query = this.entityManager.createQuery(FIND_ALL_ACCOUNTS);
        query.setHint("org.hibernate.cacheable", true);
        query.setHint("org.hibernate.cacheRegion", FIND_ALL_ACCOUNTS_CACHE_REGION);
        
        @SuppressWarnings("unchecked")
        final List<ILocalAccountPerson> accounts = query.getResultList();
        return accounts;
    }

    @Transactional
    public ILocalAccountPerson updateAccount(ILocalAccountPerson account) {
        Validate.notNull(account, "account can not be null");
        
        this.entityManager.persist(account);
        
        return account;
    }

    @Transactional
    public void deleteAccount(ILocalAccountPerson account) {
        Validate.notNull(account, "definition can not be null");
        
        final ILocalAccountPerson persistentAccount;
        if (this.entityManager.contains(account)) {
            persistentAccount = account;
        }
        else {
            persistentAccount = this.entityManager.merge(account);
        }
        
        this.entityManager.remove(persistentAccount);
    }

    public List<ILocalAccountPerson> getPeople(LocalAccountQuery query) {
        
        // TODO: This implementation doesn't actually look at all the attribute
        // values
        String queryString = "select distinct account from LocalAccountPersonImpl account join account.attributes attr join attr.values value where ";
        String separator = "";
        int count = 0;
        TreeMap<String, String> params = new TreeMap<String, String>();
        
        // if a username has been specified, append it to the query
        if (query.getName() != null) {
            queryString = queryString.concat(separator + "account.name = :name" + count);
            params.put("name" + count, query.getName());
            separator = " or ";
        }
        
        // for each specified attribute, append a clause to the query
        for (Map.Entry<String, List<String>> entry : query.getAttributes().entrySet()) {
            String v = entry.getValue().get(0);
            if (v instanceof String && !StringUtils.isBlank((String) v)) {
                String name = entry.getKey();
                String value = (String) v;
                queryString = queryString.concat(separator + "(attr.name = :name" + count + " and lower(value) like :value" + count + ")"); 
                params.put("name" + count, name);
                params.put("value" + count, "%".concat(value.toLowerCase()).concat("%"));
                if (count == 0) {
                    separator = " or ";
                }
                count++;
            }
            
        }
        final Query jpaQuery = this.entityManager.createQuery(queryString);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            jpaQuery.setParameter(entry.getKey(), entry.getValue());
        }
        
        @SuppressWarnings("unchecked")
        final List<ILocalAccountPerson> accounts = jpaQuery.getResultList();
        return accounts;
    }

    public Set<String> getCurrentAttributeNames() {
        final Query query = this.entityManager.createQuery("select distinct attribute.name from LocalAccountPersonAttributeImpl attribute");

        @SuppressWarnings("unchecked")
        final List<String> nameList = query.getResultList();
        final Set<String> nameSet = new HashSet<String>();
        nameSet.addAll(nameList);
        return nameSet;
    }

}
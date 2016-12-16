/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portal.groups;

import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.IBasicEntity;

/**
 * Reference implementation for <code>IEntity</code>.
 * @author Dan Ellentuck
 * @version $Revision$
 */
public class EntityImpl extends GroupMemberImpl implements IEntity {
    protected EntityIdentifier entityIdentifier;
/**
 * EntityImpl constructor
 */
public EntityImpl(String newEntityKey, Class newEntityType) throws GroupsException
{
    this(new EntityIdentifier(newEntityKey, newEntityType));
}
/**
 * EntityImpl constructor
 */
public EntityImpl(EntityIdentifier ei) throws GroupsException
{
    super(ei);
    Integer id = org.jasig.portal.EntityTypes.getEntityTypeID(ei.getType());
    String key = id + "." + ei.getKey();
    entityIdentifier = new EntityIdentifier(key, org.jasig.portal.EntityTypes.LEAF_ENTITY_TYPE);
}

/**
 * @return org.jasig.portal.EntityIdentifier
 */
public EntityIdentifier getEntityIdentifier() {
    return entityIdentifier;
}

/**
 * Returns the key of the underlying entity.
 * @return java.lang.String
 */
public java.lang.String getKey() {
    return getUnderlyingEntityIdentifier().getKey();
}
/**
 * Returns the type of the underyling entity.
 * @return java.lang.Class
 */
public Class<? extends IBasicEntity> getLeafType()
{
    return getUnderlyingEntityIdentifier().getType();
}
/**
 * Returns this object's type, as opposed to the type of its
 * underlying entity.
 */
public Class getType()
{
    return getLeafType();
}

/**
 * Returns a String that represents the value of this object.
 * @return a string representation of the receiver
 */
public String toString()
{
    String clsName = getLeafType().getName();
    return "EntityImpl (" + clsName + ") "  + getKey();
}
}

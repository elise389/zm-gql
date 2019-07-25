/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra GraphQL Extension
 * Copyright (C) 2019 Synacor, Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.graphql.models.outputs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.zimbra.common.gql.GqlConstants;

import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.types.GraphQLType;

/**
 * @author Zimbra API Team
 * @package com.zimbra.graphql.models.outputs
 * @copyright Copyright © 2019
 */
@GraphQLType(name=GqlConstants.CLASS_GQL_MAILBOX_METADATA, description="Contains mailbox metadata")
public class GQLMailboxMetadata {

    private String section;
    private Map<String, Object> metadata;

    /**
     * @return the section
     */
    @GraphQLQuery(name=GqlConstants.SECTION, description="Section for which the mailbox metadata is fetched")
    public String getSection() {
        return section;
    }

    /**
     * @param section the section to set
     */
    public void setSection(String section) {
        this.section = section;
    }

    /**
     * @return the metadata
     */
    @GraphQLQuery(name=GqlConstants.METADATA, description="Metadata for mailbox")
    public Map<String, String> getMetadata() {
        Map<String, String> result = new HashMap<String, String>();
        metadata.forEach((key, value) -> {
            String val = null;
            if (value instanceof Map) {
                val = Joiner.on(",").withKeyValueSeparator("=").join((Map<?, ?>) value);
            } else if (value instanceof List) {
                val = String.join(",", (List) value);
            } else {
                val = value.toString();
            }
            result.put(key, val);
        });
        return result;
    }

    /**
     * @param metadata the metadata to set
     */
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @GraphQLQuery(name=GqlConstants.SIZE, description="Size of metadata map")
    public int size() {
        if (metadata == null) {
            return 0;
        }
        return metadata.size();
    }

    @GraphQLQuery(name=GqlConstants.IS_EMPTY, description="Whether metadata map is empty or not")
    public boolean isEmpty() {
        if (metadata == null) {
            return true;
        }
        return metadata.isEmpty();
    }
}
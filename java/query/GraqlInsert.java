/*
 * Copyright (C) 2021 Grakn Labs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package graql.lang.query;

import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.variable.ThingVariable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static graql.lang.common.GraqlToken.Command.INSERT;
import static graql.lang.common.exception.ErrorMessage.NO_VARIABLE_IN_SCOPE_INSERT;

public class GraqlInsert extends GraqlWritable.InsertOrDelete {

    public GraqlInsert(List<ThingVariable<?>> variables) {
        this(null, variables);
    }

    GraqlInsert(@Nullable GraqlMatch.Unfiltered match, List<ThingVariable<?>> variables) {
        super(INSERT, match, validInsertVars(match, variables));
    }

    static List<ThingVariable<?>> validInsertVars(@Nullable GraqlMatch.Unfiltered match, List<ThingVariable<?>> variables) {
        if (match != null) {
            if (variables.stream().noneMatch(var -> var.isNamed() && match.namedVariablesUnbound().contains(var.toUnbound())
                    || var.variables().anyMatch(nestedVar -> match.namedVariablesUnbound().contains(nestedVar.toUnbound())))) {
                throw GraqlException.of(NO_VARIABLE_IN_SCOPE_INSERT.message(variables, match.namedVariablesUnbound()));
            }
        }
        return variables;
    }

    public Optional<GraqlMatch.Unfiltered> match() {
        return Optional.ofNullable(match);
    }

    public List<ThingVariable<?>> variables() { return variables; }
}

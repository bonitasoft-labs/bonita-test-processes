package org.bonitasoft.example

import org.bonitasoft.engine.api.APIClient

interface Resource {

    fun deploy(client: APIClient)

}
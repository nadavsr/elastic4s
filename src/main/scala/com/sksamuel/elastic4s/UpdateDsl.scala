package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.source.DocumentSource
import org.elasticsearch.action.WriteConsistencyLevel
import org.elasticsearch.action.support.replication.ReplicationType
import org.elasticsearch.action.update.UpdateRequestBuilder
import org.elasticsearch.common.xcontent.{ XContentBuilder, XContentFactory }
import org.elasticsearch.index.VersionType
import org.elasticsearch.script.ScriptService.ScriptType

/** @author Stephen Samuel */
trait UpdateDsl extends IndexesTypesDsl {

  def update(id: Any) = new UpdateExpectsIndex(id.toString)
  class UpdateExpectsIndex(id: String) {
    def in(indexesTypes: IndexesTypes): UpdateDefinition = new UpdateDefinition(indexesTypes, id)
  }

  class UpdateDefinition(indexesTypes: IndexesTypes, id: String) extends BulkCompatibleDefinition {

    val _builder = new UpdateRequestBuilder(ProxyClients.client)
      .setIndex(indexesTypes.index)
      .setType(indexesTypes.typ.orNull)
      .setId(id)

    def build = _builder.request

    private def _fieldsAsXContent(fields: Iterable[FieldValue]): XContentBuilder = {
      val source = XContentFactory.jsonBuilder().startObject()
      fields.foreach(_.output(source))
      source.endObject()
    }

    def script(script: String): UpdateDefinition = {
      _builder.setScript(script, ScriptType.INLINE)
      this
    }

    def detectNoop(detectNoop: Boolean): this.type = {
      _builder.setDetectNoop(detectNoop)
      this
    }

    def docAsUpsert(fields: (String, Any)*): this.type = docAsUpsert(fields.toMap)
    def docAsUpsert(iterable: Iterable[(String, Any)]): this.type = docAsUpsert(iterable.toMap)
    def docAsUpsert(map: Map[String, Any]): this.type = {
      _builder.setDocAsUpsert(true)
      doc(map)
    }
    def doc(fields: (String, Any)*): this.type = doc(fields.toMap)
    def doc(iterable: Iterable[(String, Any)]): this.type = doc(iterable.toMap)
    def doc(map: Map[String, Any]): this.type = {
      _builder.setDoc(_fieldsAsXContent(FieldsMapper.mapFields(map)))
      this
    }

    def doc(source: DocumentSource) = {
      _builder.setDoc(source.json)
      this
    }
    def routing(routing: String): this.type = {
      _builder.setRouting(routing)
      this
    }
    def params(entries: (String, Any)*): this.type = params(entries.toMap)
    def params(map: Map[String, Any]): this.type = {
      map.foreach(arg => _builder.addScriptParam(arg._1, arg._2))
      this
    }
    def retryOnConflict(retryOnConflict: Int): this.type = {
      _builder.setRetryOnConflict(retryOnConflict)
      this
    }
    def parent(parent: String): this.type = {
      _builder.setParent(parent)
      this
    }
    def refresh(refresh: Boolean): this.type = {
      _builder.setRefresh(refresh)
      this
    }
    def replicationType(repType: ReplicationType): this.type = {
      _builder.setReplicationType(repType)
      this
    }
    def consistencyLevel(consistencyLevel: WriteConsistencyLevel): this.type = {
      _builder.setConsistencyLevel(consistencyLevel)
      this
    }
    def docAsUpsert: UpdateDefinition = docAsUpsert(shouldUpsertDoc = true)
    def docAsUpsert(shouldUpsertDoc: Boolean): this.type = {
      _builder.setDocAsUpsert(shouldUpsertDoc: Boolean)
      this
    }
    def lang(scriptLang: String): this.type = {
      _builder.setScriptLang(scriptLang)
      this
    }
    def upsert(map: Map[String, Any]): this.type = {
      _builder.setUpsert(_fieldsAsXContent(FieldsMapper.mapFields(map)))
      this
    }
    def upsert(fields: (String, Any)*): this.type = upsert(fields.toMap)
    def upsert(iterable: Iterable[(String, Any)]): this.type = upsert(iterable.toMap)

    def scriptedUpsert(upsert: Boolean): this.type = {
      _builder.setScriptedUpsert(upsert)
      this
    }
    def version(version: Long): this.type = {
      _builder.setVersion(version)
      this
    }
    def version(versionType: VersionType): this.type = {
      _builder.setVersionType(versionType)
      this
    }
  }
}

{{/*
Application repository
*/}}
{{- define "application.repository" -}}
{{- regexReplaceAll "\\W+" .Values.image.repository "-" }}
{{- end }}

{{/*
Application name
*/}}
{{- define "application.name" -}}
{{- if .Values.nameOverride }}
{{- .Values.nameOverride }}
{{- else }}
{{- include "application.repository" . }}
{{- end }}
{{- end }}

{{/*
Application version
*/}}
{{- define "application.version" -}}
{{- regexReplaceAll "\\W+" .Values.app.version "-" }}
{{- end }}

{{/*
Application fullname
*/}}
{{- define "application.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride }}
{{- else }}
{{- printf "%s-%s" (include "application.name" .) (include "application.version" .) }}
{{- end }}
{{- end }}

{{/*
Application labels
*/}}
{{- define "application.labels" -}}
app.kubernetes.io/name: {{ include "application.name" . }}
app.kubernetes.io/version: {{ .Values.app.version | quote }}
{{- end }}

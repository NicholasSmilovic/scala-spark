# Curriculum 10 — Production Reliability Capstone

## Purpose

Turn the tested, deployed batch pipeline into an operable production-style system. Add explicit configuration and secret boundaries, enforce data-quality gates, support idempotent retries and bounded backfills, emit useful run evidence, schedule safely, validate changes in CI, and make cost and operational ownership visible.

The capstone uses isolated development resources. “Production-ready” means the contracts and controls are demonstrated; it does not authorize deployment to a real production environment.

## Project boundary

- Preserve exact transformation and Delta-table correctness from prior curricula.
- Extend the deployed Scala application and Declarative Automation Bundle rather than creating an unrelated demo.
- Parameterize environment, input/output identifiers, batch range, and run ID.
- Retrieve secrets only through the approved runtime mechanism; never place secret values in Git, bundle YAML, command history, or logs.
- Use development schedules, notifications, tables, and identities.
- Keep every destructive or cleanup target explicit and learner-approved.

Do not introduce production credentials, unbounded backfills, silent schema evolution, destructive retries, real customer data, or automatic production promotion.

## Milestone 1 — Make configuration and secret ownership explicit

**Learner outcomes**

- Define typed application configuration with required and optional values.
- Separate non-secret configuration from secret references.
- Validate environment and path/table allowlists before starting Spark work.
- Redact sensitive values from errors and structured logs.
- Test valid, missing, malformed, and unsafe configurations.

**Completion evidence**

- Tests prove fail-fast configuration behavior and log redaction.
- Development bundle configuration contains references, not secret values.
- The learner identifies application, platform, and operator ownership for each setting.

## Milestone 2 — Enforce data-quality gates and audit outcomes

**Learner outcomes**

- Define exact schema, nullability, uniqueness, accepted/rejected count, and domain checks.
- Persist per-run audit metrics with run ID, batch boundary, counts, and quality status.
- Fail the batch before publishing silver/gold changes when a blocking threshold is violated.
- Retain diagnosable rejected data without leaking sensitive fields.

**Completion evidence**

- Tests cover passing, warning, and blocking quality outcomes.
- A failed quality gate leaves protected downstream tables unchanged.
- Audit records reconcile input, accepted, rejected, duplicate, and output counts.

## Milestone 3 — Support retries and bounded backfills safely

**Learner outcomes**

- Make one batch rerunnable through stable keys and deterministic merge behavior.
- Define an explicit inclusive/exclusive batch-range contract.
- Execute a bounded multi-batch backfill in development.
- Distinguish transient infrastructure retries from deterministic data/code failures.
- Prove that retrying or backfilling does not duplicate logical output.

**Completion evidence**

- Exact snapshots prove idempotent retry and bounded backfill behavior.
- Invalid or unbounded backfill requests fail before mutation.
- The learner explains when automatic retry is safe and when operator review is required.

## Milestone 4 — Add observability, alerts, and an operational runbook

**Learner outcomes**

- Emit structured run-start, quality, merge, output, duration, and run-end events with one correlation/run ID.
- Link job-run logs to Delta audit history and output versions.
- Define development alert conditions for job failure, quality failure, and missing expected completion.
- Exercise one safe failure and follow a written triage/recovery path.

**Completion evidence**

- One successful and one failed development run can be reconstructed from logs, audit records, and table history.
- Alerts target learner-approved development endpoints.
- Runbook states ownership, diagnosis, retry, backfill, rollback/repair boundary, and escalation conditions.

## Milestone 5 — Validate delivery, scheduling, and cost controls

**Learner outcomes**

- Add CI checks for formatting/compilation, focused tests, full regression tests, JAR packaging, and bundle validation.
- Define a development schedule with concurrency and timeout controls.
- Require reviewed configuration for deployment and keep production promotion disabled.
- Record compute choice, runtime duration, input/output volume, and avoidable cost drivers.
- Perform one end-to-end release rehearsal from clean checkout through validated development run.

**Completion evidence**

- CI and bundle validation pass from a clean state.
- Scheduled development run completes with exact output and audit evidence.
- Concurrency, timeout, retry, and cost assumptions are explicit.
- The learner can explain the release path and the controls still required before real production use.

## Completion boundary

Curriculum 10 completes the core batch data-engineering journey: tested transformations, transactional incremental tables, cloud deployment, distributed execution evidence, and operational reliability controls. Optional specializations—streaming, JDBC ingestion, orchestration, advanced governance, and machine learning—begin only after this boundary.

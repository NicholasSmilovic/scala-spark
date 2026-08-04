# Curriculum 08 — First Azure Databricks Scala Deployment

## Purpose

Package the tested incremental Scala pipeline as a JAR and deploy it as an isolated Azure Databricks job through a Declarative Automation Bundle. Replace local paths with explicit job parameters, use learner-approved development storage, run the job twice, and verify outputs and logs.

## External prerequisite gate

This curriculum requires explicit learner approval and access to an Azure Databricks development workspace. Before any remote mutation, verify:

- Unity Catalog availability and a learner-approved catalog/schema/volume or equivalent isolated storage boundary.
- Permission to upload artifacts and create/run development jobs.
- A supported compute choice; do not assume serverless Scala availability.
- Current Databricks CLI and authenticated development profile.
- JDK/runtime compatibility for the chosen Databricks Runtime and bundle template.

Never deploy to production, staging, shared non-test data, or another person's workspace resources by default.

## Project boundary

- Preserve completed local curricula and regression tests.
- Package one main class with string parameters for input, output/table locations, and run identity.
- Keep Spark and Databricks-provided libraries out of the packaged application artifact where the selected runtime provides them.
- Define infrastructure and job resources through version-controlled Declarative Automation Bundle configuration.
- Use a development target with user-scoped names and paths.
- Keep credentials out of source, bundle YAML, logs, and Git.

Do not introduce production service principals, Terraform, streaming, multi-workspace promotion, production schedules, or destructive cleanup in this curriculum.

## Milestone 1 — Establish the deployment contract and prerequisites

**Learner outcomes**

- Record the approved workspace host, development target, storage boundary, compute choice, and required permissions without recording secrets.
- Verify CLI version and authentication.
- Define the main-class parameter contract and local validation behavior.
- Fail clearly on missing or malformed parameters.

**Completion evidence**

- Prerequisite checklist is satisfied with development-only targets.
- Local tests prove parameter parsing and reject unsafe or missing paths.

## Milestone 2 — Build a Databricks-compatible Scala JAR

**Learner outcomes**

- Configure the sbt packaging strategy for one executable main class.
- Mark runtime-provided dependencies correctly.
- Build the JAR reproducibly.
- Inspect the artifact contents and run the smallest supported local smoke test.

**Completion evidence**

- Local regression suite passes.
- JAR build succeeds and contains the application classes without unintended credentials or duplicate Spark runtime payloads.
- The learner distinguishes compilation dependencies from libraries provided by the remote runtime.

## Milestone 3 — Define and validate a Declarative Automation Bundle

**Learner outcomes**

- Add the one required root `databricks.yml` plus a separate job resource definition.
- Define an artifact build, user-scoped development target, Scala JAR task, main class, parameters, and compatible compute.
- Run bundle validation before deployment.
- Inspect the resolved configuration for accidental shared or production targets.

**Completion evidence**

- Bundle validation succeeds.
- Reviewed configuration resolves only to approved development resources.
- The learner explains the difference between building an artifact, deploying resource definitions, and running a job.

## Milestone 4 — Deploy and run the development job

**Learner outcomes**

- Deploy the bundle to the approved development target.
- Verify the uploaded JAR and created job.
- Run the job with isolated input/output locations.
- Inspect the run URL, task logs, parameters, and exact Delta outputs.

**Completion evidence**

- Deployment and job run succeed.
- Exact cloud table/output assertions match the locally tested contract.
- No production or shared non-test resources were modified.

## Milestone 5 — Prove repeatability and document the handoff

**Learner outcomes**

- Run the same job parameters again and verify logical idempotency.
- Run one new batch and verify deterministic progression.
- Record the validate/deploy/run workflow, resource ownership, safe cleanup targets, and common failure checks.
- Leave any remote resources in the learner-approved state rather than deleting them implicitly.

**Completion evidence**

- Two repeatable runs and one new-batch run have exact evidence.
- A concise development runbook identifies configuration, artifact, job, logs, storage, and cleanup ownership.

## Completion boundary

Curriculum 08 completes first Scala JAR packaging, Declarative Automation Bundle validation/deployment, isolated Azure Databricks job execution, and repeatable cloud output verification. Stop there. Curriculum 09 owns realistic-scale distributed execution analysis.

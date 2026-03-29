# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This project uses **OpenSpec** - a spec-driven workflow system for managing software development changes. The workflow is structured around creating proposals, designs, specs, and tasks before implementation.

## OpenSpec Workflow Commands

The project uses the `openspec` CLI for managing changes. Key commands:

```bash
# List all active changes
openspec list --json

# Create a new change
openspec new change "<name>"

# Check change status (shows artifacts and their completion)
openspec status --change "<name>" --json

# Get instructions for creating an artifact
openspec instructions <artifact-id> --change "<name>" --json

# Get apply instructions (implementation context)
openspec instructions apply --change "<name>" --json
```

## OPSX Slash Commands

Four workflow commands are available:

| Command | Purpose |
|---------|---------|
| `/opsx:explore` | Thinking/discovery mode - explore ideas without implementing |
| `/opsx:propose` | Create a new change with all artifacts (proposal, design, tasks) |
| `/opsx:apply` | Implement tasks from a change |
| `/opsx:archive` | Archive a completed change |

### Typical Workflow Flow

1. **Explore** (`/opsx:explore`) - Think through the problem, investigate codebase, clarify requirements
2. **Propose** (`/opsx:propose <name>`) - Create change with proposal.md, design.md, tasks.md
3. **Apply** (`/opsx:apply`) - Implement tasks one by one, marking them complete
4. **Archive** (`/opsx:archive`) - Move completed change to archive

## Project Structure

```
openspec/
├── config.yaml        # Schema configuration (spec-driven)
├── changes/           # Active changes live here
│   └── <name>/        # Each change has its own directory
│       ├── .openspec.yaml
│       ├── proposal.md
│       ├── design.md
│       ├── tasks.md
│       └── specs/     # Delta specs (changes to capability specs)
└── specs/             # Main capability specs (permanent specifications)
```

## Schema: spec-driven

The project uses the `spec-driven` schema (defined in `openspec/config.yaml`). This schema expects:

- **proposal.md** - What and why (problem, goals, scope, non-goals)
- **design.md** - How (architecture, components, data models, APIs)
- **tasks.md** - Implementation steps (checklist format)
- **specs/** - Delta specs that modify main capability specs

## Explore Mode Rules

When using `/opsx:explore`:
- This is for **thinking only** - never implement features
- May read files, search code, investigate the codebase
- May create OpenSpec artifacts (capturing thinking, not implementing)
- Use ASCII diagrams liberally to visualize ideas
- Don't auto-capture insights - offer to save them
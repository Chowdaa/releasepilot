import { FormEvent, useEffect, useState } from 'react';

const api = import.meta.env.VITE_API_URL || '/api';
type Release = { id:number; name:string; targetDate:string; status:string };
type Dashboard = { releaseName:string; decision:'GO'|'NO_GO'; reasons:string[]; requirements:number; requirementsWithTests:number; testCases:number; testRuns:number; passedRuns:number; failedRuns:number; openDefects:number; openBlockers:number; passRate:number };
type Requirement = { id:number; title:string; description:string; priority:'CRITICAL'|'HIGH'|'MEDIUM'|'LOW' };
type TestCase = { id:number; title:string; requirementTitle:string; type:string };
type TestRun = { id:number; testCaseTitle:string; result:string; executionType:string };
type Draft = { title:string; preconditions:string; steps:string; expectedResult:string; priority:'CRITICAL'|'HIGH'|'MEDIUM'|'LOW'; type:'UI'|'API' };

export default function App() {
  const [releases, setReleases] = useState<Release[]>([]);
  const [selected, setSelected] = useState<number>();
  const [dashboard, setDashboard] = useState<Dashboard>();
  const [requirements, setRequirements] = useState<Requirement[]>([]);
  const [testCases, setTestCases] = useState<TestCase[]>([]);
  const [testRuns, setTestRuns] = useState<TestRun[]>([]);
  const [drafts, setDrafts] = useState<Draft[]>([]);
  const [draftRequirementId, setDraftRequirementId] = useState<number>();
  const [draftSource, setDraftSource] = useState('');
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');

  const refreshEvidence = async (releaseId:number) => {
    const [dashboardResponse, requirementsResponse, casesResponse, runsResponse] = await Promise.all([
      fetch(`${api}/releases/${releaseId}/dashboard`),
      fetch(`${api}/releases/${releaseId}/requirements`),
      fetch(`${api}/releases/${releaseId}/test-cases`),
      fetch(`${api}/releases/${releaseId}/test-runs`)
    ]);
    if (!dashboardResponse.ok) throw new Error('Unable to load dashboard.');
    const importedRequirements = requirementsResponse.ok ? await requirementsResponse.json() : [];
    setDashboard(await dashboardResponse.json());
    setRequirements(importedRequirements);
    setTestCases(casesResponse.ok ? await casesResponse.json() : []);
    setTestRuns(runsResponse.ok ? await runsResponse.json() : []);
    setDraftRequirementId(current => current ?? importedRequirements[0]?.id);
  };

  const load = async () => {
    try {
      const response = await fetch(`${api}/releases`);
      if (!response.ok) throw new Error();
      const data=await response.json();
      setReleases(data);
      if (data[0]) setSelected(current => current ?? data[0].id);
    } catch { setError('Cannot reach the API. Start the backend on port 8080.'); }
  };

  useEffect(()=>{ load(); },[]);
  useEffect(()=>{ if (selected) refreshEvidence(selected).catch((reason:Error)=>setError(reason.message)); },[selected]);

  const createRelease = async (event:FormEvent<HTMLFormElement>) => {
    event.preventDefault(); const form=new FormData(event.currentTarget);
    const r=await fetch(`${api}/releases`, {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({name:form.get('name'),targetDate:form.get('targetDate')})});
    if (!r.ok) return setError('Release could not be created.');
    const created=await r.json(); setReleases([...releases,created]); setSelected(created.id); setNotice(`Created ${created.name}.`); event.currentTarget.reset();
  };

  const importCsv = async (event:FormEvent<HTMLFormElement>) => {
    event.preventDefault(); if (!selected) return;
    const file=new FormData(event.currentTarget).get('file') as File;
    if (!file?.name) return setError('Choose a Jira CSV export first.');
    const body=new FormData(); body.append('file',file);
    const r=await fetch(`${api}/releases/${selected}/requirements/import`,{method:'POST',body});
    if (!r.ok) return setError('Import failed. Use a CSV with Summary, Description, Priority, and Issue Type columns.');
    const result=await r.json(); setNotice(`Imported ${result.importedCount} requirements.${result.skippedRows.length ? ` Skipped ${result.skippedRows.length} rows.` : ''}`); refreshEvidence(selected); event.currentTarget.reset();
  };

  const recordRun = async (event:FormEvent<HTMLFormElement>) => {
    event.preventDefault(); const form=new FormData(event.currentTarget);
    const r=await fetch(`${api}/test-runs`, {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({testCaseId:Number(form.get('testCaseId')),result:form.get('result'),executionType:form.get('executionType'),notes:form.get('notes')})});
    if (!r.ok) return setError('Test result could not be saved. Select a test case and complete all required fields.');
    setNotice('Test result recorded. Release decision refreshed.'); if (selected) refreshEvidence(selected); event.currentTarget.reset();
  };

  const logDefect = async (event:FormEvent<HTMLFormElement>) => {
    event.preventDefault(); if (!selected) return; const form=new FormData(event.currentTarget); const testRunId = form.get('testRunId');
    const r=await fetch(`${api}/defects`, {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({releaseId:selected,testRunId:testRunId ? Number(testRunId) : null,summary:form.get('summary'),severity:form.get('severity')})});
    if (!r.ok) return setError('Defect could not be logged. Complete the summary and severity.');
    setNotice('Defect logged. Release decision refreshed.'); refreshEvidence(selected); event.currentTarget.reset();
  };

  const generateDrafts = async (event:FormEvent<HTMLFormElement>) => {
    event.preventDefault(); if (!selected || !draftRequirementId) return setError('Select an imported requirement first.');
    const form = new FormData(event.currentTarget);
    const r = await fetch(`${api}/releases/${selected}/test-case-drafts`, {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({requirementId:draftRequirementId,acceptanceCriterion:form.get('acceptanceCriterion')})});
    if (!r.ok) return setError('Drafts could not be generated. Check the acceptance criterion and try again.');
    const result = await r.json(); setDrafts(result.drafts); setDraftSource(result.source); setNotice(result.reviewNotice); setError('');
  };

  const approveDraft = async (draft:Draft) => {
    if (!draftRequirementId) return;
    const r = await fetch(`${api}/test-cases`, {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({...draft, requirementId:draftRequirementId})});
    if (!r.ok) return setError('Approved draft could not be saved as a test case.');
    setDrafts(current => current.filter(candidate => candidate.title !== draft.title));
    setNotice(`Approved and created test case: ${draft.title}.`);
    if (selected) refreshEvidence(selected);
  };

  return <main>
    <header><p className="eyebrow">RELEASE QUALITY WORKSPACE</p><h1>ReleasePilot</h1><p>Evidence-based readiness for every release.</p></header>
    <section className="toolbar"><label>Release<select value={selected ?? ''} onChange={e=>setSelected(Number(e.target.value))}>{releases.map(r=><option key={r.id} value={r.id}>{r.name}</option>)}</select></label><form onSubmit={createRelease}><input name="name" placeholder="New release name" required/><input name="targetDate" type="date" required/><button>Create release</button></form></section>
    <section className="import"><strong>Import Jira requirements</strong><span>Upload Story or Task rows from a Jira CSV export.</span><form onSubmit={importCsv}><input name="file" type="file" accept=".csv,text/csv" required/><button>Import CSV</button></form></section>
    {error && <p className="error">{error}</p>}{notice && <p className="notice">{notice}</p>}
    {dashboard && <>
      <section className={`decision ${dashboard.decision === 'GO' ? 'go' : 'no-go'}`}><div><span>RELEASE DECISION</span><strong>{dashboard.decision === 'GO' ? 'GO' : 'NO-GO'}</strong></div><ul>{dashboard.reasons.map(reason=><li key={reason}>{reason}</li>)}</ul></section>
      <section className="metrics"><Metric label="Requirements" value={`${dashboard.requirementsWithTests}/${dashboard.requirements}`}/><Metric label="Test cases" value={dashboard.testCases}/><Metric label="Pass rate" value={`${dashboard.passRate}%`}/><Metric label="Open defects" value={dashboard.openDefects}/><Metric label="Blockers" value={dashboard.openBlockers}/><Metric label="Failed runs" value={dashboard.failedRuns}/></section>
      <section className="workflows">
        <form className="card workflow" onSubmit={recordRun}><h2>Record a test result</h2><p>Capture execution evidence without using the API directly.</p><select name="testCaseId" required defaultValue=""><option value="" disabled>Select a test case</option>{testCases.map(testCase=><option key={testCase.id} value={testCase.id}>{testCase.title} · {testCase.type}</option>)}</select><div className="form-row"><select name="result" defaultValue="PASS"><option>PASS</option><option>FAIL</option><option>BLOCKED</option><option>SKIPPED</option></select><input name="executionType" placeholder="Execution type (e.g. UI)" required/></div><textarea name="notes" placeholder="Evidence or observation (optional)" maxLength={2000}/><button disabled={!testCases.length}>Save test result</button></form>
        <form className="card workflow" onSubmit={logDefect}><h2>Log a defect</h2><p>Record release risk and let blockers affect the decision.</p><input name="summary" placeholder="Defect summary" maxLength={240} required/><div className="form-row"><select name="severity" defaultValue="MAJOR"><option>BLOCKER</option><option>CRITICAL</option><option>MAJOR</option><option>MINOR</option></select><select name="testRunId" defaultValue=""><option value="">No linked test run</option>{testRuns.map(run=><option key={run.id} value={run.id}>{run.result}: {run.testCaseTitle}</option>)}</select></div><button>Log defect</button></form>
      </section>
      <section className="card draft-workflow"><h2>AI-assisted test-case drafts</h2><p>Generate three drafts, review their coverage, then explicitly approve each one before it becomes a test case. {draftSource && <strong>Source: {draftSource === 'OPENAI' ? 'OpenAI' : 'local safety fallback'}.</strong>}</p><form onSubmit={generateDrafts}><select value={draftRequirementId ?? ''} onChange={event=>setDraftRequirementId(Number(event.target.value))} required><option value="" disabled>Select a requirement</option>{requirements.map(requirement=><option key={requirement.id} value={requirement.id}>{requirement.title}</option>)}</select><textarea name="acceptanceCriterion" placeholder="Paste or write one acceptance criterion" required maxLength={2000}/><button disabled={!requirements.length}>Generate drafts for review</button></form>{drafts.length > 0 && <div className="drafts">{drafts.map(draft=><article className="draft" key={draft.title}><span>{draft.type} · {draft.priority}</span><h3>{draft.title}</h3><p><b>Preconditions:</b> {draft.preconditions}</p><p><b>Steps:</b> {draft.steps}</p><p><b>Expected:</b> {draft.expectedResult}</p><button type="button" onClick={()=>approveDraft(draft)}>Approve & create test case</button></article>)}</div>}</section>
      <section className="card"><h2>How this demo works</h2><p>Import Jira stories, generate and approve test cases, record test evidence and defects, then review the explainable release decision.</p></section>
    </>}
  </main>;
}

function Metric({label,value}:{label:string|number;value:string|number}) { return <article><span>{label}</span><strong>{value}</strong></article>; }

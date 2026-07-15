const api='/api/v1';
const $=id=>document.getElementById(id);
async function get(path,options){const r=await fetch(api+path,options);if(!r.ok)throw new Error(`Falha HTTP ${r.status}`);const body=await r.json();if(!body.sucesso)throw new Error(body.mensagem);return body.dados}
function rows(items){if(!items.length)return '<p class="meta">Nenhum trabalho encontrado.</p>';return `<table><thead><tr><th>Nome</th><th>Impressora</th><th>Tipo</th><th>Status</th><th>Criação</th></tr></thead><tbody>${items.map(x=>`<tr><td>${safe(x.nome)}</td><td>${safe(x.impressora)}</td><td>${safe(x.tipoConteudo)}</td><td>${safe(x.status)}</td><td>${new Date(x.dataCriacao).toLocaleString('pt-BR')}</td></tr>`).join('')}</tbody></table>`}
const safe=v=>String(v??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
async function status(){const x=await get('/status');$('estado').textContent=x.status;$('versao').textContent=x.versao;$('fila').textContent=x.trabalhosNaFila;$('totalImpressoras').textContent=x.impressorasEncontradas;$('sistema').textContent=x.sistemaOperacional}
async function impressoras(){const xs=await get('/impressoras');$('listaImpressoras').innerHTML=xs.length?xs.map(x=>`<article class="card"><strong>${safe(x.nome)}</strong><span class="${x.status==='READY'?'ok':'bad'}">${safe(x.status)}</span><div class="meta">${x.padrao?'Impressora padrão':'Impressora instalada'}</div></article>`).join(''):'<p class="meta">Nenhuma impressora encontrada.</p>'}
async function trabalhos(){$('listaTrabalhos').innerHTML=rows(await get('/trabalhos?limite=100'))}
async function historico(){$('listaHistorico').innerHTML=rows(await get('/historico?limite=100'))}
async function logs(){const x=await get('/logs?linhas=300');$('conteudoLog').textContent=x.linhas.join('\n')||'Nenhuma linha de log disponível.'}
const actions={impressoras,trabalhos,historico,logs};
async function run(fn){try{$('erro').textContent='';await fn()}catch(e){$('erro').textContent='Não foi possível atualizar: '+e.message}}
document.querySelectorAll('nav button').forEach(b=>b.onclick=()=>{document.querySelectorAll('nav button').forEach(x=>x.classList.remove('active'));document.querySelectorAll('.tab').forEach(x=>x.classList.add('hidden'));b.classList.add('active');$(b.dataset.tab).classList.remove('hidden');run(actions[b.dataset.tab])});
document.querySelectorAll('[data-action]').forEach(b=>b.onclick=()=>run(actions[b.dataset.action]));
Promise.all([run(status),run(impressoras)]);setInterval(()=>{if(!document.hidden)run(status)},10000);
